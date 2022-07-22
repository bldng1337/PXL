struct Vertexdata {
	//Texture
	float t_index;
	vec2 t_coord;
};
//Vertex Shader
#if VERT 
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_Texc;
layout (location = 2) in float a_Tex;
uniform mat4 proj;
out Vertexdata vdata;

void main()
{
    vdata.t_index=a_Tex;
	vdata.t_coord=a_Texc;
 	gl_Position=proj*vec4(a_Pos,1.0f);
}
#endif
#if FRAG
layout (location = 0) out vec4 o_Color;
layout (binding = 0) uniform sampler2D u_Textures[32];
in Vertexdata vdata;
void main()
{
    vec4 alb=texture(u_Textures[int(vdata.t_index)],vdata.t_coord);
    if(alb.a<=0)
		discard;
    o_Color=alb;
}
#endif