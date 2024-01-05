export function jsonFormatter(json: string) {
    return JSON.stringify(JSON.parse(json), null, 2)
}