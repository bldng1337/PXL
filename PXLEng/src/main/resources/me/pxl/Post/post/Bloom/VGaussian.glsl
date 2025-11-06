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
precision highp float;

vec4 kernel4x4(ivec2 pos){
	return imageLoad(hdr_tex,pos+ivec2(0,0))/**0.25f+
		   imageLoad(hdr_tex,pos+ivec2(1,0))*0.25f+
		   imageLoad(hdr_tex,pos+ivec2(0,1))*0.25f+
		   imageLoad(hdr_tex,pos+ivec2(1,1))*0.25f*/;
}

void main(){
    ivec2 bpos = ivec2(gl_GlobalInvocationID.xy);
    vec4 hdrColor=kernel4x4(bpos)*0.38774+
    			  kernel4x4(bpos+ivec2(0,1))*0.24477+
				  kernel4x4(bpos+ivec2(0,-1))*0.24477+
				  kernel4x4(bpos+ivec2(0,2))*0.06136+
				  kernel4x4(bpos+ivec2(0,-2))*0.06136;
    imageStore( hdr_tex, bpos, hdrColor );
}
#endif
