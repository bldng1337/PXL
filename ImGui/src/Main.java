import java.awt.Desktop;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.system.MemoryUtil;

import ImGui.ImUI;
import ImGui.sequencer.Sequencer;
import ImGui.sequencer.Sequencer.Sequence;
import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.ImNodesContext;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.extension.imnodes.flag.ImNodesPinShape;
import imgui.extension.implot.ImPlot;
import imgui.extension.memedit.MemoryEditor;
import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.NodeEditorConfig;
import imgui.extension.nodeditor.NodeEditorContext;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImLong;
import imgui.type.ImString;
import me.pxl.Engine;
import me.pxl.ECS.Component;

public class Main {
    float[] f=new float[20];
	public Main() {
		System.setProperty("java.library.path", "C:\\Users\\BF\\git\\Prodigium\\Editor\\libs");
		Engine e=Engine.getEngine();
		Object o=new Component();
		for(int i=0;i<20;i++) {
			f[i]=1;
		}
		@SuppressWarnings("unused")
		ImUI ui=new ImUI(e,()->{
			
//			ImGui.showDemoWindow();
//			ImBoolean i=new ImBoolean(true);
//			Main.show(i);
//			Main.show(i, g);
//			Main.showDrag(i);
//			Main.showEdit(i);
//			Main.showNode(i, g2);
//			Main.showText(i);
			
			 if (ImGui.begin("Sequencer Test")) {
				 List<Sequence> ltest=new ArrayList<>();
				 ltest.add(new Sequence("yeet",0,100));
				 ltest.add(new Sequence("ya",120,300));
				 Sequencer.beginSequence(ltest, 40,f);
				 
				 Sequencer.endSequence(f);
			 }
			 ImGui.end();
		});
		e.update();
		System.exit(0);
	}
	Graph g=new Graph();
	Graph g2=new Graph();
	
	public static void main(String[] args) {
		new Main();
	}
	
	
    private static final String URL = "https://github.com/epezent/implot/tree/555ff68";
    private static final ImBoolean showDemo = new ImBoolean(false);

    private static final Integer[] xs = {0, 1, 2, 3, 4, 5};
    private static final Integer[] ys = {0, 1, 2, 3, 4, 5};
    private static final Integer[] ys1 = {0, 0, 1, 2, 3, 4};
    private static final Integer[] ys2 = {1, 2, 3, 4, 5, 6};

    static {
        ImPlot.createContext();
    }

    public static void show(ImBoolean showImPlotWindow) {
        ImGui.setNextWindowSize(500, 400, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("ImPlot Demo", showImPlotWindow)) {
            ImGui.text("This a demo for ImPlot");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();
            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ImGui.checkbox("Show ImPlot Built-In Demo", showDemo);

            if (ImPlot.beginPlot("Example Plot")) {
                ImPlot.plotShaded("Shaded", xs, ys1, ys2);
                ImPlot.plotLine("Line", xs, ys);
                ImPlot.plotBars("Bars", xs, ys);
                ImPlot.endPlot();
            }
            

            if (ImPlot.beginPlot("Example Scatterplot")) {
                ImPlot.plotScatter("Scatter", xs, ys);
                ImPlot.endPlot();
            }

            if (showDemo.get()) {
                ImPlot.showDemoWindow(showDemo);
            }
        }

        ImGui.end();
    }
    
    private static final NodeEditorContext CONTEXT;

    static {
        NodeEditorConfig config = new NodeEditorConfig();
        config.setSettingsFile(null);
        CONTEXT = new NodeEditorContext(config);
    }

    public static void show(final ImBoolean showImNodeEditorWindow, final Graph graph) {
        ImGui.setNextWindowSize(500, 400, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 200, ImGuiCond.Once);
        if (ImGui.begin("imgui-node-editor Demo", showImNodeEditorWindow)) {
            ImGui.text("This a demo graph editor for imgui-node-editor");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();
            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ImGui.button("Navigate to content")) {
                NodeEditor.navigateToContent(1);
            }

            NodeEditor.setCurrentEditor(CONTEXT);
            NodeEditor.begin("Node Editor");

            for (Graph.GraphNode node : graph.nodes.values()) {
                NodeEditor.beginNode(node.nodeId);
                ImGui.text(node.getName());
                NodeEditor.beginPin(node.getInputPinId(), NodeEditorPinKind.Input);
                ImGui.text("<");
                NodeEditor.endPin();

                ImGui.sameLine();

                NodeEditor.beginPin(node.getOutputPinId(), NodeEditorPinKind.Output);
                ImGui.text(">");
                NodeEditor.endPin();

                NodeEditor.endNode();
            }
            if (NodeEditor.beginCreate()) {
                final ImLong a = new ImLong();
                final ImLong b = new ImLong();
                if (NodeEditor.queryNewLink(a, b)) {
                    final Graph.GraphNode source = graph.findByOutput(a.get());
                    final Graph.GraphNode target = graph.findByInput(b.get());
                    if (source != null && target != null && source.outputNodeId != target.nodeId && NodeEditor.acceptNewItem()) {
                        source.outputNodeId = target.nodeId;
                    }
                }
            }
            NodeEditor.endCreate();

            int uniqueLinkId = 1;
            for (Graph.GraphNode node : graph.nodes.values()) {
                if (graph.nodes.containsKey(node.outputNodeId)) {
                    NodeEditor.link(uniqueLinkId++, node.getOutputPinId(), graph.nodes.get(node.outputNodeId).getInputPinId());
                }
            }

            NodeEditor.suspend();
            final long nodeWithContextMenu = NodeEditor.getNodeWithContextMenu();
            if (nodeWithContextMenu != -1) {
                ImGui.openPopup("node_context");
                ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), (int) nodeWithContextMenu);
            }

            if (ImGui.isPopupOpen("node_context")) {
                final int targetNode = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
                if (ImGui.beginPopup("node_context")) {
                    if (ImGui.button("Delete " + graph.nodes.get(targetNode).getName())) {
                        graph.nodes.remove(targetNode);
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }

            if (NodeEditor.showBackgroundContextMenu()) {
                ImGui.openPopup("node_editor_context");
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                	
                    final Graph.GraphNode node = graph.createGraphNode();
                    final float canvasX = NodeEditor.toCanvasX(ImGui.getMousePosX());
                    final float canvasY = NodeEditor.toCanvasY(ImGui.getMousePosY());
                    NodeEditor.setNodePosition(node.nodeId, canvasX, canvasY);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }

            NodeEditor.resume();
            NodeEditor.end();
        }
        ImGui.end();
    }
    
    class Graph {
        public int nextNodeId = 1;
        public int nextPinId = 100;

        public final Map<Integer, GraphNode> nodes = new HashMap<>();

        public Graph() {
            final GraphNode first = createGraphNode();
            final GraphNode second = createGraphNode();
            first.outputNodeId = second.nodeId;
        }

        public GraphNode createGraphNode() {
            final GraphNode node = new GraphNode(nextNodeId++, nextPinId++, nextPinId++);
            this.nodes.put(node.nodeId, node);
            return node;
        }

        public GraphNode findByInput(final long inputPinId) {
            for (GraphNode node : nodes.values()) {
                if (node.getInputPinId() == inputPinId) {
                    return node;
                }
            }
            return null;
        }

        public GraphNode findByOutput(final long outputPinId) {
            for (GraphNode node : nodes.values()) {
                if (node.getOutputPinId() == outputPinId) {
                    return node;
                }
            }
            return null;
        }

        final class GraphNode {
            public final int nodeId;
            public final int inputPinId;
            public final int outputPinId;

            public int outputNodeId = -1;

            public GraphNode(final int nodeId, final int inputPinId, final int outputPintId) {
                this.nodeId = nodeId;
                this.inputPinId = inputPinId;
                this.outputPinId = outputPintId;
            }

            public int getInputPinId() {
                return inputPinId;
            }

            public int getOutputPinId() {
                return outputPinId;
            }

            public String getName() {
                return "Node " + (char) (64 + nodeId);
            }
        }
    }
    
    private static final TextEditor EDITOR = new TextEditor();

    static {
        TextEditorLanguageDefinition lang = TextEditorLanguageDefinition.glsl();

        String[] ppnames = {
            "NULL", "PM_REMOVE",
            "ZeroMemory", "DXGI_SWAP_EFFECT_DISCARD", "D3D_FEATURE_LEVEL", "D3D_DRIVER_TYPE_HARDWARE", "WINAPI", "D3D11_SDK_VERSION", "assert"};
        String[] ppvalues = {
            "#define NULL ((void*)0)",
            "#define PM_REMOVE (0x0001)",
            "Microsoft's own memory zapper function\n(which is a macro actually)\nvoid ZeroMemory(\n\t[in] PVOID  Destination,\n\t[in] SIZE_T Length\n); ",
            "enum DXGI_SWAP_EFFECT::DXGI_SWAP_EFFECT_DISCARD = 0",
            "enum D3D_FEATURE_LEVEL",
            "enum D3D_DRIVER_TYPE::D3D_DRIVER_TYPE_HARDWARE  = ( D3D_DRIVER_TYPE_UNKNOWN + 1 )",
            "#define WINAPI __stdcall",
            "#define D3D11_SDK_VERSION (7)",
            " #define assert(expression) (void)(                                                  \n" +
                "    (!!(expression)) ||                                                              \n" +
                "    (_wassert(_CRT_WIDE(#expression), _CRT_WIDE(__FILE__), (unsigned)(__LINE__)), 0) \n" +
                " )"
        };

        // Adding custom preproc identifiers
        Map<String, String> preprocIdentifierMap = new HashMap<>();
        for (int i = 0; i < ppnames.length; ++i) {
            preprocIdentifierMap.put(ppnames[i], ppvalues[i]);
        }
        lang.setPreprocIdentifiers(preprocIdentifierMap);

        String[] identifiers = {
            "HWND", "HRESULT", "LPRESULT","D3D11_RENDER_TARGET_VIEW_DESC", "DXGI_SWAP_CHAIN_DESC","MSG","LRESULT","WPARAM", "LPARAM","UINT","LPVOID",
                "ID3D11Device", "ID3D11DeviceContext", "ID3D11Buffer", "ID3D11Buffer", "ID3D10Blob", "ID3D11VertexShader", "ID3D11InputLayout", "ID3D11Buffer",
                "ID3D10Blob", "ID3D11PixelShader", "ID3D11SamplerState", "ID3D11ShaderResourceView", "ID3D11RasterizerState", "ID3D11BlendState", "ID3D11DepthStencilState",
                "IDXGISwapChain", "ID3D11RenderTargetView", "ID3D11Texture2D", "TextEditor" };
        String[] idecls = {
            "typedef HWND_* HWND", "typedef long HRESULT", "typedef long* LPRESULT", "struct D3D11_RENDER_TARGET_VIEW_DESC", "struct DXGI_SWAP_CHAIN_DESC",
                "typedef tagMSG MSG\n * Message structure","typedef LONG_PTR LRESULT","WPARAM", "LPARAM","UINT","LPVOID",
                "ID3D11Device", "ID3D11DeviceContext", "ID3D11Buffer", "ID3D11Buffer", "ID3D10Blob", "ID3D11VertexShader", "ID3D11InputLayout", "ID3D11Buffer",
                "ID3D10Blob", "ID3D11PixelShader", "ID3D11SamplerState", "ID3D11ShaderResourceView", "ID3D11RasterizerState", "ID3D11BlendState", "ID3D11DepthStencilState",
                "IDXGISwapChain", "ID3D11RenderTargetView", "ID3D11Texture2D", "class TextEditor" };

        // Adding custom identifiers
        Map<String, String> identifierMap = new HashMap<>();
        for (int i = 0; i < ppnames.length; ++i) {
            identifierMap.put(identifiers[i], idecls[i]);
        }
        lang.setIdentifiers(identifierMap);

        EDITOR.setLanguageDefinition(lang);

        // Adding error markers
        Map<Integer, String> errorMarkers = new HashMap<>();
        errorMarkers.put(1, "Expected '>'");
        EDITOR.setErrorMarkers(errorMarkers);

        EDITOR.setTextLines(new String[]{
            "#include <iostream",
            "",
            "int main() {",
            "   std::cout << \"Hello, World!\" << std::endl;",
            "}"
        });
    }

    @SuppressWarnings("unused")
	public static void showText(final ImBoolean showImColorTextEditWindow) {
        ImGui.setNextWindowSize(500, 400);
        if (ImGui.begin("Text Editor", showImColorTextEditWindow,
                ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.MenuBar)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("File")) {
                    if (ImGui.menuItem("Save")) {
                        String textToSave = EDITOR.getText();
                        /// save text....
                    }

                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Edit")) {
                    final boolean ro = EDITOR.isReadOnly();
                    if (ImGui.menuItem("Read-only mode", "", ro)) {
                        EDITOR.setReadOnly(!ro);
                    }

                    ImGui.separator();

                    if (ImGui.menuItem("Undo", "ALT-Backspace", !ro && EDITOR.canUndo())) {
                        EDITOR.undo(1);
                    }
                    if (ImGui.menuItem("Redo", "Ctrl-Y", !ro && EDITOR.canRedo())) {
                        EDITOR.redo(1);
                    }

                    ImGui.separator();

                    if (ImGui.menuItem("Copy", "Ctrl-C", EDITOR.hasSelection())) {
                        EDITOR.copy();
                    }
                    if (ImGui.menuItem("Cut", "Ctrl-X", !ro && EDITOR.hasSelection())) {
                        EDITOR.cut();
                    }
                    if (ImGui.menuItem("Delete", "Del", !ro && EDITOR.hasSelection())) {
                        EDITOR.delete();
                    }
                    if (ImGui.menuItem("Paste", "Ctrl-V", !ro && ImGui.getClipboardText() != null)) {
                        EDITOR.paste();
                    }

                    ImGui.endMenu();
                }

                ImGui.endMenuBar();
            }

            int cposX = EDITOR.getCursorPositionLine();
            int cposY = EDITOR.getCursorPositionColumn();

            String overwrite = EDITOR.isOverwrite() ? "Ovr" : "Ins";
            String canUndo = EDITOR.canUndo() ? "*" : " ";

            ImGui.text(cposX + "/" + cposY + " " + EDITOR.getTotalLines() + " lines | " + overwrite + " | " + canUndo);

            EDITOR.render("TextEditor");
        }
        ImGui.end();
    }
    
    private static final ImNodesContext NCONTEXT = new ImNodesContext();

    private static final ImInt LINK_A = new ImInt();
    private static final ImInt LINK_B = new ImInt();

    static {
        ImNodes.createContext();
    }

    public static void showNode(final ImBoolean showImNodesWindow, final Graph graph) {
        ImGui.setNextWindowSize(500, 400, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("ImNodes Demo", showImNodesWindow)) {
            ImGui.text("This a demo graph editor for ImNodes");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();
            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ImNodes.editorContextSet(NCONTEXT);
            ImNodes.beginNodeEditor();

            for (Graph.GraphNode node : graph.nodes.values()) {
                ImNodes.beginNode(node.nodeId);

                ImNodes.beginNodeTitleBar();
                ImGui.text(node.getName());
                ImNodes.endNodeTitleBar();
                ImNodes.beginInputAttribute(node.getInputPinId(), ImNodesPinShape.TriangleFilled);
                ImGui.text("In");
                ImNodes.endInputAttribute();
                ImGui.sameLine();

                ImNodes.beginOutputAttribute(node.getOutputPinId());
                ImGui.text("Out");
                ImNodes.endOutputAttribute();

                ImNodes.endNode();
            }

            int uniqueLinkId = 1;
            for (Graph.GraphNode node : graph.nodes.values()) {
                if (graph.nodes.containsKey(node.outputNodeId)) {
                    ImNodes.link(uniqueLinkId++, node.getOutputPinId(), graph.nodes.get(node.outputNodeId).getInputPinId());
                }
            }

            final boolean isEditorHovered = ImNodes.isEditorHovered();

            ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight);
            ImNodes.endNodeEditor();

            if (ImNodes.isLinkCreated(LINK_A, LINK_B)) {
                final Graph.GraphNode source = graph.findByOutput(LINK_A.get());
                final Graph.GraphNode target = graph.findByInput(LINK_B.get());
                if (source != null && target != null && source.outputNodeId != target.nodeId) {
                    source.outputNodeId = target.nodeId;
                }
            }

            if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                final int hoveredNode = ImNodes.getHoveredNode();
                if (hoveredNode != -1) {
                    ImGui.openPopup("node_context");
                    ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), hoveredNode);
                } else if (isEditorHovered) {
                    ImGui.openPopup("node_editor_context");
                }
            }

            if (ImGui.isPopupOpen("node_context")) {
                final int targetNode = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
                if (ImGui.beginPopup("node_context")) {
                    if (ImGui.button("Delete " + graph.nodes.get(targetNode).getName())) {
                        graph.nodes.remove(targetNode);
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                    final Graph.GraphNode node = graph.createGraphNode();
                    ImNodes.setNodeScreenSpacePos(node.nodeId, ImGui.getMousePosX(), ImGui.getMousePosY());
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }
    
    private static ByteBuffer demoData;
    private static MemoryEditor memoryEditor;
    private static ImBoolean showingMemoryEditor = new ImBoolean(false);

    static {
        demoData = MemoryUtil.memASCII("Welcome to the demo for Dear ImGui Memory Editor." +
            "\n The git repo is located at " + URL + "." +
            "You can use this memory editor to view the raw memory values at some pointer location.");

        memoryEditor = new MemoryEditor();
    }

    public static void showEdit(ImBoolean showImGuiMemoryEditor) {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("ImGuiMemoryEditor Demo", showImGuiMemoryEditor)) {
            ImGui.text("This a demo for ImGui MemoryEditor");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();
            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ImGui.checkbox("Memory Editor", showingMemoryEditor);
            if (showingMemoryEditor.get()) {
                memoryEditor.drawWindow("Example Data", MemoryUtil.memAddress(demoData), demoData.capacity());
            }
        }
        ImGui.end();
    }
    
    private static final String PAYLOAD_TYPE = "PAYLOAD";

    private static final StringPayload STRING_PAYLOAD = new StringPayload();
    private static final IntegerPayload INTEGER_PAYLOAD = new IntegerPayload();

    private static final AtomicInteger CLASS_SPECIFIC_PAYLOAD = new AtomicInteger(23);

    private static String data = "No Data...";

    public static void showDrag(final ImBoolean showDragNDropWindow) {
        if (ImGui.begin("Drag'N'Drop Demo", showDragNDropWindow, ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("Drag from here:");

            ImGui.inputText("String payload", STRING_PAYLOAD.input, ImGuiInputTextFlags.CallbackResize);
            setupPayload(STRING_PAYLOAD);

            ImGui.inputInt("Integer payload", INTEGER_PAYLOAD.input);
            setupPayload(INTEGER_PAYLOAD);

            ImGui.button("Class specific payload");
            setupClassSpecificPayload();

            ImGui.separator();

            ImGui.text("Drop here any:");
            ImGui.button(data, 100, 50);
            setupTarget();

            ImGui.separator();

            ImGui.text("Drop here string payload only");
            ImGui.button(data, 100, 50);
            setupStringPayloadTarget();

            ImGui.separator();

            ImGui.text("Drop here class specific payload only");
            ImGui.button(data, 100, 50);
            setupClassSpecificPayloadTarget();
        }
        ImGui.end();
    }

    private static void setupPayload(final Payload<?> payload) {
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
            ImGui.setDragDropPayload(PAYLOAD_TYPE, payload);
            ImGui.text("Dragging: " + payload.getData());
            ImGui.endDragDropSource();
        }
    }

    private static void setupClassSpecificPayload() {
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(CLASS_SPECIFIC_PAYLOAD);
            ImGui.text("Dragging class specific payload");
            ImGui.endDragDropSource();
        }
    }

    private static void setupTarget() {
        if (ImGui.beginDragDropTarget()) {
            Payload<?> payload = ImGui.acceptDragDropPayload(PAYLOAD_TYPE);
            if (payload != null) {
                data = String.valueOf(payload.getData());
            }
            ImGui.endDragDropTarget();
        }
    }

    /**
     * Type safe example. ImGui will show that it can accept payload, but payload itself will be null.
     */
    private static void setupStringPayloadTarget() {
        if (ImGui.beginDragDropTarget()) {
            StringPayload payload = ImGui.acceptDragDropPayload(PAYLOAD_TYPE, StringPayload.class);
            if (payload != null) {
                data = payload.getData();
            }
            ImGui.endDragDropTarget();
        }
    }

    /**
     * Class specific example. We can bind our payload to a specific class, so it will be 100% type safe.
     */
    private static void setupClassSpecificPayloadTarget() {
        if (ImGui.beginDragDropTarget()) {
            AtomicInteger payload = ImGui.acceptDragDropPayload(AtomicInteger.class);
            if (payload != null) {
                data = String.valueOf(payload.get());
            }
            ImGui.endDragDropTarget();
        }
    }

    private interface Payload<T> {
        T getData();
    }

    private static final class StringPayload implements Payload<String> {
        ImString input = new ImString("You can drag inputs as well");

        @Override
        public String getData() {
            return input.get();
        }
    }

    private static final class IntegerPayload implements Payload<Integer> {
        ImInt input = new ImInt();

        @Override
        public Integer getData() {
            return input.get();
        }
    }
}
