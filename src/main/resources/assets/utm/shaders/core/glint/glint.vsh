#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform int FogShape;

uniform vec2 UVScale;
uniform vec2 ScrollOffset;
uniform vec2 ScrollSpeed;

out float vertexDistance;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexDistance = fog_distance(Position, FogShape);

    vec2 uv = (TextureMat * vec4(UV0 * UVScale, 0.0, 1.0)).xy;
    texCoord0 = uv + ScrollOffset * ScrollSpeed;
}