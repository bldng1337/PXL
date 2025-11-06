#if COMP
/*
uvec3 gl_NumWorkGroups	global work group size we gave to glDispatchCompute()
uvec3 gl_WorkGroupSize	local work group size we defined with layout
uvec3 gl_WorkGroupID	position of current invocation in global work group
uvec3 gl_LocalInvocationID	position of current invocation in local work group
uvec3 gl_GlobalInvocationID	unique index of current invocation in global work group
uint gl_LocalInvocationIndex	1d index representation of gl_LocalInvocationID
*/
layout(local_size_x = 8, local_size_y = 8, local_size_z = 1) in;//workgroups
layout(rgba16f,binding=0) uniform image2D hdr_tex;
layout(rgba16f,binding=1) uniform image2D out_tex;
precision highp float;
uniform float threshhold;
uniform int shouldsan;

vec4 smp(ivec2 pos){
//	vec2 uv=vec2(pos)/vec2(imageSize(out_tex));
	vec4 hdrColor = imageLoad(hdr_tex,pos);
	if(shouldsan==0)
		return hdrColor;
	if((hdrColor.r>threshhold||hdrColor.g>threshhold||hdrColor.b>threshhold))//||hdrColor.a>0
	    	return hdrColor+vec4(0.01f,0.01f,0.01f,0.0f);
	return vec4(0.0f);
}
vec4 kernel4x4(ivec2 pos){
	return smp(pos+ivec2(0,0))*0.25f+
		   smp(pos+ivec2(1,0))*0.25f+
		   smp(pos+ivec2(0,1))*0.25f+
		   smp(pos+ivec2(1,1))*0.25f;
}
void main(){
    ivec2 pos = ivec2(gl_GlobalInvocationID.xy);
    ivec2 bpos=pos*2;
//    vec4 hdrColor=kernel4x4(bpos-ivec2(-1,-1))+
//		kernel4x4(bpos-ivec2(-1,0))+
//		kernel4x4(bpos-ivec2(0,-1))+
//		kernel4x4(bpos-ivec2(0,0));
//    hdrColor/=4.0f;
    vec4 hdrColor=kernel4x4(bpos)*0.38774+
    			  kernel4x4(bpos+ivec2(2,0))*0.24477+
				  kernel4x4(bpos+ivec2(-2,0))*0.24477+
				  kernel4x4(bpos+ivec2(4,0))*0.06136+
				  kernel4x4(bpos+ivec2(-4,0))*0.06136;
    imageStore( out_tex, pos, hdrColor );
}
#endif
