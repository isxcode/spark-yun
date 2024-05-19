export default class ResizeEngine {

    eleId: string
    resizeable: boolean
    clientX: number | null
    clientY: number | null
    minW: number
    minH: number
    direc: string
    c: any
    callback: any
    containerId?: string
    containerInstance?: any
    direction?: string    // e: 右边, n: 上边, s: 下边, w: 左边

    constructor(eleId: string, containerId?: string, direction?: string) {
        this.resizeable = false
        this.clientX = null
        this.clientY = null
        this.minW = 8
        this.minH = 8
        this.direc = ''
        this.direction = direction

        this.containerId = containerId

        this.eleId = eleId
    }

    setElementResize(callback?: any): void {
        this.callback = callback
        this.c = document.getElementById(this.eleId)
        if (this.containerId) {
            this.containerInstance = document.getElementById(this.containerId)
            this.containerInstance.addEventListener('mousemove', this.move.bind(this))
            this.containerInstance.addEventListener('mouseup', this.up.bind(this))
        } else {
            window.addEventListener('mousemove', this.move.bind(this))
            window.addEventListener('mouseup', this.up.bind(this))
        }
        this.c.addEventListener('mousedown', this.down.bind(this))
    }

    removeElementResize(): void {
        if (this.containerId) {
            this.containerInstance.removeEventListener('mousemove', this.move.bind(this))
            this.containerInstance.removeEventListener('mouseup', this.up.bind(this))
        } else {
            window.removeEventListener('mousemove', this.move.bind(this))
            window.removeEventListener('mousedown', this.down.bind(this))
            window.removeEventListener('mouseup', this.up.bind(this))
        }
        this.c.removeEventListener('mousedown', this.down.bind(this))
    }

    // 获取鼠标所在div的位置
    getDirection(ev: any): string {
        let xP, yP, offset, dir;
        dir = '';

        xP = ev.offsetX;
        yP = ev.offsetY;
        offset = 10;

        if (yP < offset) dir += 'n';
        else if (yP > this.c.offsetHeight - offset) dir += 's';
        if (xP < offset) dir += 'w';
        else if (xP > this.c.offsetWidth - offset) dir += 'e';

        return dir;
    }

    up(): void {
        console.log('松开')
        this.resizeable = false
    }

    down(e: any): void {
        let d = this.getDirection(e)
        // 当位置为四个边和四个角时才开启尺寸修改
        if (d !== '') {
            this.resizeable = true
            this.direc = d
            this.clientX = e.clientX
            this.clientY = e.clientY
        }
    }

    move(e: any): void {
        let d = this.getDirection(e)
        let cursor
        if (d === '') cursor = 'default';
        else cursor = d + '-resize';
        // 修改鼠标显示效果
        this.c.style.cursor = cursor;
        // 当开启尺寸修改时，鼠标移动会修改div尺寸
        if (this.resizeable) {
            // 鼠标按下的位置在右边，修改宽度
            if (this.direc.indexOf('e') !== -1 && this.direction === 'e') {
                this.c.style.width = Math.max(this.minW, this.c.offsetWidth + (e.clientX - this.clientX)) + 'px'
                this.clientX = e.clientX
                if (this.callback && this.callback instanceof Function) {
                    this.callback()
                }
            }
            // 鼠标按下的位置在上部，修改高度
            if (this.direc.indexOf('n') !== -1 && this.direction === 'n') {
                this.c.style.height = Math.max(this.minH, this.c.offsetHeight + (this.clientY - e.clientY)) + 'px'
                this.clientY = e.clientY
                if (this.callback && this.callback instanceof Function) {
                    this.callback()
                }
            }
            // 鼠标按下的位置在底部，修改高度
            if (this.direc.indexOf('s') !== -1 && this.direction === 's') {
                this.c.style.height = Math.max(this.minH, this.c.offsetHeight + (e.clientY - this.clientY)) + 'px'
                this.clientY = e.clientY
                if (this.callback && this.callback instanceof Function) {
                    this.callback()
                }
            }
            // 鼠标按下的位置在左边，修改宽度
            if (this.direc.indexOf('w') !== -1 && this.direction === 'w') {
                this.c.style.width = Math.max(this.minW, this.c.offsetWidth + (this.clientX - e.clientX)) + 'px'
                this.clientX = e.clientX
                if (this.callback && this.callback instanceof Function) {
                    this.callback()
                }
            }
        }
    }
}