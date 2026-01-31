#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GlintAlpha;
uniform vec4 GlintColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0) * ColorModulator + (FogColor * .0001);
    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd) * GlintAlpha;
    float maxC = max(max(texColor.r, texColor.g), texColor.b);
    vec3 boost = max(texColor.rgb - (maxC * .015), 0.0);
    vec3 finalRgb = (boost / max(maxC, 0.001)) * GlintColor.rgb * maxC;
    finalRgb = pow(finalRgb, vec3(1.4)) * 1.1;
    float finalAlpha = pow(texColor.a * GlintColor.a, 3.0) * 2.2;
    finalRgb += (FogColor.rgb * 0.000001);

    fragColor = vec4(finalRgb * fade, clamp(finalAlpha * fade, 0.0, 1.0));
}