#include<../STD_ShaderUtils.glsl>

uniform float time;

void main()
{
	#if FRAG
	vec4 alb=albedo;
	if(alb.a<=0)
		discard;
	setCol(alb);
	//setOccluder(alb);
	#endif
	setPos(VPOS);
	setTUV(vec2(TUV.x,abs(TUV.y-1)));
}
