<template>
  <div class="zqy-bpmn">
    <el-row class="mb-4">
      <el-button
        type="primary"
        @click="resetTransform"
      >
        重置
      </el-button>
      <el-button
        type="primary"
        @click="updatePos"
      >
        修改属性
      </el-button>
      <el-button
        type="primary"
        @click="toggleclass"
      >
        修改样式
      </el-button>
      <el-button
        type="primary"
        @click="logToObject"
      >
        查看属性
      </el-button>
    </el-row>

    <VueFlow
      v-model="elements"
      fit-view-on-init
      class="my-flow"
    >
      <Background />
      <Panel :position="PanelPosition.TopRight">
        <div>
          <label for="ishidden">
            hidden
            <input
              id="ishidden"
              v-model="isHidden"
              type="checkbox"
            >
          </label>
        </div>
      </Panel>
      <Controls />
    </VueFlow>
  </div>
</template>

<script lang="ts" setup>
import '@vue-flow/core/dist/style.css'
import '@vue-flow/core/dist/theme-default.css'

import { Background, Panel, PanelPosition, Controls } from '@vue-flow/additional-components'
import { VueFlow, useVueFlow } from '@vue-flow/core'
import { ref, watch } from 'vue'

const data = [
  {
    id: '1', type: 'input', label: 'Node 1', position: {
      x: 250, y: 5 
    } 
  },
  {
    id: '2', label: 'Node 2', position: {
      x: 100, y: 100 
    } 
  },
  {
    id: '3', label: 'Node 3', position: {
      x: 400, y: 100 
    } 
  },
  {
    id: '4', label: 'Node 4', position: {
      x: 400, y: 200 
    } 
  },
  {
    id: 'e1-2', source: '1', target: '2', animated: true 
  },
  {
    id: 'e1-3', source: '1', target: '3' 
  }
]
let elements = ref(data)

const isHidden = ref(false)

let {
  onPaneReady, onNodeDragStop, onConnect, addEdges, setTransform, toObject, nodes, edges 
} = useVueFlow()

watch(isHidden, () => {
  nodes.value.forEach((n) => (n.hidden = isHidden.value))
  edges.value.forEach((e) => (e.hidden = isHidden.value))
})

onPaneReady(({
  fitView 
}) => {
  fitView()
})
onNodeDragStop((e) => console.log('drag stop', e))
onConnect((params) => addEdges([ params ]))

const updatePos = () => {
  nodes.value.forEach((el) => {
    el.position = {
      x: Math.random() * 400,
      y: Math.random() * 400
    }
  })
}

const logToObject = () => {
  // ElMessage.info(JSON.stringify(toObject()));
}
const resetTransform = () => {
  elements.value = data
  setTransform({
    x: 0, y: 0, zoom: 1 
  })
}
const toggleclass = () => nodes.value.forEach((el) => (el.class = el.class === 'node-light' ? 'node-dark' : 'node-light'))
</script>

<style lang="scss">
.zqy-bpmn {
  .my-flow {
    margin: 10px;
    height: 400px;

    .node-light {
      background: none;
    }
    .node-dark {
      background: #eeeeee;
    }
  }
}
</style>
