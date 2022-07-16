struct Vertexdata {
	vec2 t_coord;
    vec4 t_color;
};

#if VERT
layout (location = 0) in vec2 a_Pos;
layout (location = 1) in vec2 a_UV;
layout (location = 2) in vec4 a_Color;

uniform mat4 proj;

out Vertexdata vdata;

void main(){
	vdata.t_coord=a_UV;
    vdata.t_color=a_Color;
	gl_Position=proj*vec4(a_Pos,0.0f,1.0f);
}
#endif
#if FRAG
layout (location = 0) out vec4 o_Color;

layout (binding = 0) uniform sampler2D u_Texture;

in Vertexdata vdata;

void main()
{
	o_Color=vdata.t_color*texture(u_Texture,vdata.t_coord);
}
#endif
