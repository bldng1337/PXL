package ImGui;
import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.callback.ImPlatformFuncViewport;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiViewportFlags;
import imgui.type.ImInt;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.DType;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.IndexType;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Backend.Generic.Texture.Textureformat;
import me.pxl.Utils.IOUtils;

public class ImGuiImplPXL {
	RenderAPI rp;
	
    // Used to store tmp renderer data
    private final ImVec2 displaySize = new ImVec2();
    private final ImVec2 framebufferScale = new ImVec2();
    private final ImVec2 displayPos = new ImVec2();
    private final ImVec4 clipRect = new ImVec4();
    
    
    private RByteBuffer rbuf;
    private RIndexBuffer ibuf;
	private Shader sh;
	private Texture font;
    
    public ImGuiImplPXL(RenderAPI r) {
    	rp=r;
    	
    	final ImGuiIO io = ImGui.getIO();
    	io.setBackendRendererName("imgui_java_impl_pxl");
    	io.addBackendFlags(ImGuiBackendFlags.RendererHasViewports);
    	io.addBackendFlags(ImGuiBackendFlags.RendererHasVtxOffset);
    	final ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        final ImInt width = new ImInt();
        final ImInt height = new ImInt();
        final ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            initPlatformInterface();
        }
    	font=r.getTex(Textureformat.RGBA8, width.get(), height.get());
    	font.load(buffer);
    	fontAtlas.setTexID(font.getID());
    	
    	rbuf=r.getBbuf(Usage.STREAM, 2,new Attrib(DType.FLOAT,2),new Attrib(DType.FLOAT,2),new Attrib(DType.UBYTE,4).setNormalized(true));
    	ibuf=r.getibuf(Usage.STREAM, 2);
    	ibuf.setDtype(IndexType.USHORT);
    	sh=r.getShader(Type.VERTEXFRAGMENT, IOUtils.stream2string(this.getClass().getResourceAsStream("ImGui.glsl")));
    }
    
    
    
	private void initPlatformInterface() {
		ImGui.getPlatformIO().setRendererRenderWindow(new ImPlatformFuncViewport() {
            @Override
            public void accept(final ImGuiViewport vp) {
                if (!vp.hasFlags(ImGuiViewportFlags.NoRendererClear)) {
                    rp.begin();
                }
                renderDrawData(vp.getDrawData());
            }
        });
	}



	public void renderDrawData(final ImDrawData drawData) {
	 if (drawData.getCmdListsCount() <= 0) {
		 return;
     }
	// Will project scissor/clipping rectangles into framebuffer space
     drawData.getDisplaySize(displaySize);           // (0,0) unless using multi-viewports
     drawData.getDisplayPos(displayPos);
     drawData.getFramebufferScale(framebufferScale); // (1,1) unless using retina display which are often (2,2)

     final float clipOffX = displayPos.x;
     final float clipOffY = displayPos.y;
     final float clipScaleX = framebufferScale.x;
     final float clipScaleY = framebufferScale.y;

     // Avoid rendering when minimized, scale coordinates for retina displays (screen coordinates != framebuffer coordinates)
     final int fbWidth = (int) (displaySize.x * framebufferScale.x);
     final int fbHeight = (int) (displaySize.y * framebufferScale.y);

     if (fbWidth <= 0 || fbHeight <= 0) {
         return;
     }
     
     {
	     final float left = displayPos.x;
	     final float right = displayPos.x + displaySize.x;
	     final float top = displayPos.y;
	     final float bottom = displayPos.y + displaySize.y;
	     Matrix4f m=new Matrix4f();
	     m.identity();
	     m.ortho2D( left,right, bottom, top);
	     sh.setVal("proj", m);
     }
     
     RenderPass rpass=rp.getRp(sh, rp.getVArr(ibuf, rbuf), rp.getScreen());
     for (int cmdListIdx = 0; cmdListIdx < drawData.getCmdListsCount(); cmdListIdx++) {
    	 try(Save<ByteBuffer> bbuf=rbuf.load(drawData.getCmdListVtxBufferSize(cmdListIdx));
    			 Save<ByteBuffer> ribuf=ibuf.loadb(drawData.getCmdListIdxBufferSize(cmdListIdx))){
    		 bbuf.getBuf().put(drawData.getCmdListVtxBufferData(cmdListIdx));
    		 ribuf
    		 .getBuf()
    		 .put(drawData.getCmdListIdxBufferData(cmdListIdx));
    		 
    	 }
    	 
    	 
    	 for (int cmdBufferIdx = 0; cmdBufferIdx < drawData.getCmdListCmdBufferSize(cmdListIdx); cmdBufferIdx++) {
    		 drawData.getCmdListCmdBufferClipRect(cmdListIdx, cmdBufferIdx, clipRect);

             final float clipMinX = (clipRect.x - clipOffX) * clipScaleX;
             final float clipMinY = (clipRect.y - clipOffY) * clipScaleY;
             final float clipMaxX = (clipRect.z - clipOffX) * clipScaleX;
             final float clipMaxY = (clipRect.w - clipOffY) * clipScaleY;

             if (clipMaxX <= clipMinX || clipMaxY <= clipMinY) {
                 continue;
             }
             rpass.setScissor(new Vector4f(clipMinX, (fbHeight - clipMaxY), (clipMaxX - clipMinX), (clipMaxY - clipMinY)));
             //TODO: Scissor
             final int textureId = drawData.getCmdListCmdBufferTextureId(cmdListIdx, cmdBufferIdx);
             final int elemCount = drawData.getCmdListCmdBufferElemCount(cmdListIdx, cmdBufferIdx);
             final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(cmdListIdx, cmdBufferIdx);
             final int vtxBufferOffset = drawData.getCmdListCmdBufferVtxOffset(cmdListIdx, cmdBufferIdx);
//             TODO: Support mby some time
             final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;
             rpass.setCount(elemCount);
             rpass.setOffset(indices);
             rpass.setVertexoffset(vtxBufferOffset);
             sh.setTexture(0, textureId);
             rp.submit(rpass);
    	 }
     }
     rpass.getV().destroy();
     
	}
	
}
