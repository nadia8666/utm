#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec4 Color;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * ColorModulator * Color;
    if (color.a < 0.1) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}