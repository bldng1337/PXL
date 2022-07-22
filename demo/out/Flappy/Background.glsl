#include<../STD_ShaderUtils.glsl>

uniform float time;
#define THRESHHOLD 1f
void main()
{
	setPos(VPOS);
	setTUV(vec2(TUV.x*5.0f+time,TUV.y));
	
	#if FRAG
	vec4 alb=samplealbedo(vec2(fract(TUV.x),TUV.y));
	vec4 filt=ceil(floor(alb+vec4(0.1f)));
	float f=filt.x+filt.y+filt.z;
	setCol(alb*vec4(f+1.0f));
	// setOccluder(vec4(f));
	#endif
}
