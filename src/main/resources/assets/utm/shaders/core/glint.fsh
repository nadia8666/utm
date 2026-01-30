#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float GlintAlpha;
uniform vec4 GlintColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0) * ColorModulator;
    if (texColor.a < 0.01) discard;

    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd) * GlintAlpha;
    fragColor = vec4(texColor.rgb * GlintColor.rgb * fade, texColor.a);
}