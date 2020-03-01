# OxygenTool
氧气工具，为java工作提供便利
# Java工具分享——httpclinet封装

## 一。背景说明

​	在进行了java开发如此之久后，开始思考，自己写的代码质量到底如何，相信大部分朋友也有相同的想法，同时也对大批量的重复代码有相当的抵触，所以开始希望总结自己对于java开发中常用工具，api的封装，希望能在未来的工作生活中，简化自己的代码，提高工作效率。

## 二。本节由来

​	现有工作中，主要任务为爬虫，不可避免的接触到了这个包，在熟悉的前提下，开始有了在此基础上进行封装简化的想法，故做出本代码

## 三。具体说明

### 1. 简化请求方式

 	所有请求相关内容在对HTTPRequest类进行实例化后进行配置，支持对URL，请求重试次数，重试等待时间，请求头，请求方法，请求体进行灵活配置

### 2. 简化HTTPClient

 	通过对HTTPClient的封装（默认配置http及https请求，请求的连接，读取等超时），可以更加轻松的管理对应cookie，代理，请求池，对请求方式进行更加简化的封装，自动对cookie进行管理

### 3. 封装请求池

​	通过对请求池的封装，增加服务的性能，配置socket相关参数 

### 4. 简化请求返回

 	HTTPResponse封装了对返回体中重点内容的提取，如：状态码，body，重点向url，下载到的entity，编码等

代码地址:https://github.com/pmerinfosafe/OxygenTool
