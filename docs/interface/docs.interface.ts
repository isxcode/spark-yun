export interface DirNode {
  id: number;
  title: string;
  domId: string;
  hLevel: number;
  pid?: number;
  isActive?: boolean,
  children?: DirNode[];
}
