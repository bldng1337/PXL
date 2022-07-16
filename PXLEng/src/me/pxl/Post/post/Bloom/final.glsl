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
layout(binding=1) uniform sampler2D mips[6];
uniform int test;

void main(){
	ivec2 pos = ivec2(gl_GlobalInvocationID.xy);
	vec2 vpos = vec2(pos)/vec2(imageSize(hdr_tex));
	vec2 pixel= vec2(0.0f,1.0f)/vec2(imageSize(hdr_tex));
	vec4 fcol=imageLoad(hdr_tex,pos);
	for(int i=0;i<test;i++){
		vec4 col =   texture(mips[i],vpos)*0.38774
					+texture(mips[i],vpos+pixel)*0.24477
					+texture(mips[i],vpos-pixel)*0.24477
					+texture(mips[i],vpos+pixel*2)*0.06136
					+texture(mips[i],vpos-pixel*2)*0.06136;
		fcol+=col;
	}
	fcol.a=1.0f;
    imageStore( hdr_tex, pos, fcol );
}
#endif
