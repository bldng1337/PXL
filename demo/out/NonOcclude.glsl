#include<STD_ShaderUtils.glsl>

void main()
{
	#if FRAG
	vec4 alb=albedo;
	if(alb.a<=0)
		discard;
	setCol(alb);
	setOccluder(vec4(0.0f));
	#endif
	setPos(VPOS);
	//setCol(albedo+vec4(1.0f));
	
}
