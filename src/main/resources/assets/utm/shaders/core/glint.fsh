#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D GlintSampler;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float GlintAlpha;

in float vertexDistance;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 ignoredColor = texture(Sampler0, texCoord0);
    vec4 texColor = texture(GlintSampler, texCoord0);

    // Vanilla glint textures rely heavily on alpha; if it's too low, we stop.
    if (texColor.a < 0.01) discard;

    // We take the red channel of the glint texture as our 'mask'
    float mask = texColor.r;

    // Use pure red, but respect the game's GlintAlpha and the ColorModulator's Alpha
    vec3 pureRed = vec3(1.0, 0.0, 0.0);
    float finalAlpha = texColor.a * GlintAlpha * ColorModulator.a;

    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd);

    // Output the red glint
    fragColor = vec4(pureRed * mask * fade, finalAlpha);
}