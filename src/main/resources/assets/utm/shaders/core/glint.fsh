#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D GlintSampler;
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
    vec4 ignoredColor = texture(Sampler0, texCoord0);
    vec4 texColor = texture(GlintSampler, texCoord0);

    if (texColor.a < 0.01) discard;

    vec3 finalColor = texColor.r * GlintColor.rgb;
    float finalAlpha = texColor.a * GlintAlpha * ColorModulator.a;

    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd);
    fragColor = vec4(finalColor * fade, finalAlpha);
}