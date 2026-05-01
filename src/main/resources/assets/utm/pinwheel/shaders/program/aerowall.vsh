layout(location = 0) in vec3 Position;
layout(location = 2) in vec2 UV0;
layout(location = 4) in vec3 Normal;

uniform float VeilRenderTime;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord;
out float vDistance;

void main() {
    float amplitude = .02;
    float freq = 2;
    float speed = .05;
    float wave = sin(Position.y * freq + VeilRenderTime * speed) * amplitude;
    vec3 dPos = Position + (Normal * wave);
    vec4 viewPos = ModelViewMat * vec4(dPos, 1.0);

    gl_Position = ProjMat * viewPos;
    texCoord = UV0;
    vDistance = length(viewPos.xyz);
}