export function jsonFormatter(json: string) {
    let result: string
    try {
        result = JSON.stringify(JSON.parse(json), null, 2)
    } catch (error) {
        result = json
    }
    return result
}