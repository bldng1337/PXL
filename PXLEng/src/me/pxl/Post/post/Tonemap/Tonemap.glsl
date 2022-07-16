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
layout(rgba8,binding=1) uniform image2D out_tex;
#define REINHARD 1
#define EXPOSURE 2
#define ACES 3


#ifdef EXPOSURE
uniform float exposure;
#endif

void main(){
    ivec2 pos = ivec2( gl_GlobalInvocationID.xy );
    vec3 hdrColor = imageLoad(hdr_tex,pos).rgb;

    #if TONEMODE == REINHARD
    hdrColor = hdrColor / (hdrColor + vec3(1.0));
    #endif
	#if TONEMODE == EXPOSURE
    hdrColor = vec3(1.0) - exp(-hdrColor * exposure);
    #endif
	#if TONEMODE == ACES
    hdrColor=(hdrColor * (vec3(2.51) * hdrColor + vec3(0.03))) / (hdrColor * (vec3(2.43) * hdrColor + vec3(0.59)) + vec3(0.14));
    #endif
    #ifdef GAMMA
    const float gamma = 2.2f;
    hdrColor = pow(hdrColor, vec3(1.0f / gamma));
    #endif

    imageStore( out_tex, pos, vec4(hdrColor, 1.0f ) );
}
#endif
