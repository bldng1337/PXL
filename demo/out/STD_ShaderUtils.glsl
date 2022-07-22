struct Vertexdata {
	//Texture
	float t_index;
	vec2 t_coord;
};

//Vertex Shader
#if VERT 
//MACROS
#define VPOS a_Pos
#define TUV a_Texc
#define setTUV(tuv) vdata.t_coord=tuv
#define setPos(pos) vdata.t_index=a_Tex;\
	                vdata.t_coord=a_Texc;\
 	                gl_Position=proj*vec4(pos,1.0f)

#define albedo
#define setCol(col)
#define setOccluder(col)
#define col
//LAYOUT
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_Texc;
layout (location = 2) in float a_Tex;
uniform mat4 proj;
out Vertexdata vdata;
#endif
//Fragment Shader
#if FRAG
//MACROS
#define albedo texture(u_Textures[int(vdata.t_index)],vdata.t_coord)
#define samplealbedo(uv) texture(u_Textures[int(vdata.t_index)],uv)
#define TUV vdata.t_coord
#define col o_Color
#define setCol(col) o_Color=col 
#define setOccluder(col) o_Occ=col

#define VPOS
#define setTUV(tuv)
#define setPos(pos)


//LAYOUT
layout (location = 0) out vec4 o_Color;
layout (location = 1) out vec4 o_Occ;
layout (binding = 0) uniform sampler2D u_Textures[32];
in Vertexdata vdata;
#endif