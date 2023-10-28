<template>
  <div class="home-footer">
    <!-- 产品 设计工具 原型工具 PRD 文档
    为什么选我们 角色功能介绍 向同事介绍我们
    服务 帮助中心 最新功能 服务条款 开发者中心 
    公司 关于我们 加入我们 成长历程 媒体报道 隐私协议 
    设计专题 Figma Sketch PS XD Axure AI 
    联系我们 010-57296155 contact@xiaopiu.com 用户交流群 经营国 中国 经营年度 2016 -->
    <div class="logo">
      <img src="~assets/images/favicon.png" alt="">
    </div>
    <div class="info">
      <div class="product">
        <div class="title">产品</div>
        <div class="content">
          <div class="item">设计工具</div>
          <div class="item">原型工具</div>
          <div class="item">PRD 文档</div>
        </div>
      </div>

      <div class="about">
        <div class="title">为什么选我们</div>
        <div class="content">
          <div class="item">角色功能介绍</div>
          <div class="item">向同事介绍我们</div>
        </div>
      </div>

      <div class="service">
        <div class="title">服务</div>
        <div class="content">
          <div class="item">帮助中心</div>
          <div class="item">最新功能</div>
          <div class="item">服务条款</div>
          <div class="item">开发者中心</div>
        </div>
      </div>

      <div class="company">
        <div class="title">公司</div>
        <div class="content">
          <div class="item">关于我们</div>
          <div class="item">加入我们</div>
          <div class="item">成长历程</div>
          <div class="item">媒体报道</div>
          <div class="item">隐私协议</div>
        </div>
      </div>

      <div class="design">
        <div class="title">设计专题</div>
        <div class="content">
          <div class="item">Figma</div>
          <div class="item">Sketch</div>
          <div class="item">PS</div>
          <div class="item">XD</div>
          <div class="item">Axure</div>
          <div class="item">AI</div>
        </div>
      </div>

      <div class="contact">
        <div class="title">联系我们</div>
        <div class="content">
          <div class="item">010-57296155</div>
          <div class="item">
            <a href="mailto:email@gmail.com"> </a>
          </div>
          <div class="item">用户交流群</div>
          <div class="item">经营国 中国</div>
          <div class="item">经营年度 2016</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
.home-footer {
  background-color: #f5f5f5;
  padding: 50px 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  .logo {
    width: 120px;
    height: 100px;
    margin-left: 80px;
    margin-bottom: 220px;
  }
  .info {
    width: 68%;
    display: flex;
    justify-content: space-between;
    padding: 0 20px;
    margin-top: 100px;
    margin-bottom: 50px;
    .title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 20px;
    }
    .content {
      display: flex;
      flex-direction: column;
      .item {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
        a {
          color: #666;
        }
      }
    }
  }
}
</style>
