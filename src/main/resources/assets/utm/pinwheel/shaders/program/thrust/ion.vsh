#veil:buffer veil:camera VeilCamera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;

uniform float VeilRenderTime;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord;
out float vDistance;
out vec3 modelPos;

void main() {
    modelPos = Position;

    float amplitude = .015;
    float freq = 10;
    float speed = 50;

    vec4 worldPos = VeilCamera.IViewMat * ModelViewMat * vec4(Position, 1.0) + vec4(VeilCamera.CameraPosition + VeilCamera.CameraBobOffset, 0);

    vec3 dPos = Position + vec3(
    sin(worldPos.x * freq + VeilRenderTime * speed) * amplitude,
    sin(worldPos.y * freq + VeilRenderTime * speed) * amplitude,
    sin(worldPos.z * freq + VeilRenderTime * speed) * amplitude
    );

    vec4 viewPos = ModelViewMat * vec4(dPos, 1.0);

    gl_Position = ProjMat * viewPos;
    texCoord = UV0;
    vDistance = length(viewPos.xyz);
}