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
layout(binding=1) uniform sampler2D light_tex;
layout(rgba8,binding=2) uniform image2D occ_tex;
uniform vec3 lightpos[100];
uniform int lights;
uniform float GlobalLight;
#define PI 3.14159265359f
#define TWOPI 6.28318530718f

void main(){
    ivec2 pos = ivec2(gl_GlobalInvocationID.xy);
    vec2 vpos=vec2(pos);
    vec3 hdrColor = imageLoad(hdr_tex,pos).rgb;
    float ill=GlobalLight;
    if(imageLoad(occ_tex,pos).a==0)
		for(int i=0;i<lights;i++){
			vec3 light=lightpos[i];
			vec2 dpos=light.xy-vpos;
			if(length(dpos)<light.z){
				float angle=atan(dpos.y/light.z,dpos.x/light.z);
				angle=(angle + PI) / (TWOPI);
				float mdist=texture(light_tex,vec2(angle,float(i)/float(lights-1))).r;
				if(length(dpos)<mdist*light.z)
					ill+=abs(light.z-length(dpos))/light.z;
			}
		}
    imageStore( hdr_tex, pos, vec4(hdrColor*vec3(ill),1.0f) );
}
#endif
