#define SIZE 100.0f
#define COL1 vec3(1.0f)
#define COL2 vec3(0.95f)

#if VERT
layout (location = 0) in vec2 a_Pos;
layout (location = 1) in vec2 a_RPos;

uniform vec3 transform;
out vec2 pos;

void main(){
    pos=a_RPos+transform.xy;
	gl_Position=vec4(a_Pos,0.0f,1.0f);
}
#endif
#if FRAG
layout (location = 0) out vec4 o_Color;

in vec2 pos;

void main()
{
    float col=mod(floor(pos.x/SIZE+floor(pos.y/SIZE)),2.0f);
    
    // Output to screen
    o_Color = vec4(mix(COL1,COL2,col),1.0);
}
#endif