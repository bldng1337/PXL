#if COMP
/*
uvec3 gl_NumWorkGroups	global work group size we gave to glDispatchCompute()
uvec3 gl_WorkGroupSize	local work group size we defined with layout
uvec3 gl_WorkGroupID	position of current invocation in global work group
uvec3 gl_LocalInvocationID	position of current invocation in local work group
uvec3 gl_GlobalInvocationID	unique index of current invocation in global work group
uint gl_LocalInvocationIndex	1d index representation of gl_LocalInvocationID
*/
layout(local_size_x = 64, local_size_y = 1, local_size_z = 1) in;//workgroups
layout(rgba8,binding=0) uniform image2D occ_tex;
layout(rgba8,binding=1) uniform image2D light_tex;
uniform vec3 lightpos[100];
uniform vec2 transform;

#define PI 3.14159265359f
#define TWOPI 6.28318530718f

void main(){
    uint id=uint(gl_GlobalInvocationID.y);// current id of light
    vec3 light=lightpos[id];// current light getting raycasted
    float siz=float(imageSize(light_tex).x);//number of rays shot out
    float angle=(float(gl_GlobalInvocationID.x)/siz)*TWOPI;//Angle of this ray
    float raylength=1.0f;//length of ray
    vec2 occsize=vec2(imageSize(occ_tex));// size of the occluder texture
    for(float i=0.0f;i<light.z;i++){
    	vec2 rpos=vec2(cos(angle)*i,sin(angle)*i)+light.xy;//pos of current lookup
        if(rpos.x<0||rpos.y<0||rpos.x>occsize.x||rpos.y>occsize.y){
            raylength=i/light.z;
            continue;
        }
    	float txt = imageLoad(occ_tex,ivec2(rpos)).a;
        if(txt>0){
            raylength=i/light.z;
            break;
        }
    }
    imageStore( light_tex, ivec2(gl_GlobalInvocationID.xy), vec4(vec3(raylength*raylength,1.0f,0.0f), 1.0f ) );
}
#endif
