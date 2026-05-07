uniform sampler2D Sampler0;
uniform float VeilRenderTime;

in vec2 texCoord;
in float vDistance;
in vec3 modelPos;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
    mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

vec4 triplanarSample(vec3 p, vec3 n, float t) {
    vec3 blending = abs(n);
    blending /= (blending.x + blending.y + blending.z);

    float ds = 1.5;

    vec2 uvX = p.zy * 2.0;
    vec2 distX = vec2(smoothNoise(uvX + t * 0.4) + sin(uvX.y * 2.0 + t), smoothNoise(uvX - t * 0.4) + cos(uvX.x * 2.0 + t)) * ds;
    vec4 colX = texture(Sampler0, fract(uvX + distX));

    vec2 uvY = p.xz * 2.0;
    vec2 distY = vec2(smoothNoise(uvY + t * 0.4) + sin(uvY.y * 2.0 + t), smoothNoise(uvY - t * 0.4) + cos(uvY.x * 2.0 + t)) * ds;
    vec4 colY = texture(Sampler0, fract(uvY + distY));

    vec2 uvZ = p.xy * 2.0;
    vec2 distZ = vec2(smoothNoise(uvZ + t * 0.4) + sin(uvZ.y * 2.0 + t), smoothNoise(uvZ - t * 0.4) + cos(uvZ.x * 2.0 + t)) * ds;
    vec4 colZ = texture(Sampler0, fract(uvZ + distZ));

    return colX * blending.x + colY * blending.y + colZ * blending.z;
}

void main() {
    float time = VeilRenderTime * 7.0;

    vec3 normal = normalize(cross(dFdx(modelPos), dFdy(modelPos)));
    vec4 sampledNoise = triplanarSample(modelPos, normal, time);

    float pNoise = smoothNoise(vec2(length(modelPos) * 2.0, time * 1.0));
    float combinedNoise = sampledNoise.r * pNoise / 2.0;

    vec3 coreColor = vec3(0.91, 0.5, 0)*.15 + .2;
    vec3 glowColor = vec3(1, .2, .1)*.15 + .2;

    vec3 finalColor = mix(coreColor, glowColor, combinedNoise) * sampledNoise.rgb * (5.0 + combinedNoise * 3.0);
    float finalAlpha = combinedNoise * 1.5;

    fragColor = vec4(finalColor, finalAlpha +.2);
}