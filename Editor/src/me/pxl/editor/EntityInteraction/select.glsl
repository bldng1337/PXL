    struct Vertexdata {
        vec2 t_coord;
    };
    #define WIDTH 2.0f
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
	layout (location = 1) out vec4 o_Occ;
    uniform vec2 size;
    in Vertexdata vdata;

    void main()
    {
        o_Color=vec4(0.0f);
		o_Occ=vec4(0.0f);
        if(vdata.t_coord.x>size.x-WIDTH||vdata.t_coord.x<WIDTH||vdata.t_coord.y>size.y-WIDTH||vdata.t_coord.y<WIDTH)
            o_Color=vec4(vec3(0.0f),1.0f);
		else
			discard;
		
    }
    #endif
