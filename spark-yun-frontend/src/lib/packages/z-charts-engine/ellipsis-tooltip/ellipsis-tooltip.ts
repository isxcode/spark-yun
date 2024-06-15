import { Placement } from 'element-plus'

export interface EllipsisTooltipProps {
  placement?: Placement
  label: string | number
}

export const ellipsisTooltipPropsDefault: EllipsisTooltipProps = {
  placement: 'top-start',
  label: ''
}
