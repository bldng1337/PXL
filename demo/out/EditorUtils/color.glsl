struct Vertexdata {
	vec2 t_coord;
};

#if VERT
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_Texc;

uniform mat4 proj;

out Vertexdata vdata;

void main(){
	vdata.t_coord=a_Texc;
	gl_Position=proj*vec4(a_Pos,1.0f);
}
#endif
#if FRAG
layout (location = 0) out vec4 o_Color;

layout (binding = 0) uniform sampler2D u_Texture;

in Vertexdata vdata;

void main()
{
	o_Color=texture(u_Texture,vdata.t_coord);
}
#endif