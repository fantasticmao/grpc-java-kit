# gRPC Java Kit

[![Actions Status](https://github.com/fantasticmao/grpc-java-kit/workflows/ci/badge.svg)](https://github.com/fantasticmao/grpc-java-kit/actions)
![JDK Version](https://img.shields.io/badge/JDK-11%2B-blue)
[![Maven Central](https://img.shields.io/maven-central/v/cn.fantasticmao.grpc-kit/grpc-kit-all.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cn.fantasticmao.grpc-kit%22)
[![image](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/fantasticmao/grpc-java-kit/blob/main/LICENSE)

## 特性列表

- 网络通讯 [gRPC-Java](https://github.com/grpc/grpc-java)
- 序列化 / 反序列化 [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview)
- 接入方式
  - [x] Java SE (JDK 11+)
  - [x] Spring Boot
- 服务注册 / 发现
  - [x] 服务分组
  - [x] ZooKeeper、~~Consul~~
- 负载均衡、流量管理
  - [x] 加权随机、~~最少连接数量~~、~~最少平均耗时~~
  - [x] 流量染色
  - [ ] 认证授权
- 容错设计
  - [ ] 超时重试、快速失败
  - [ ] 限流、熔断、降级
  - [ ] 故障检测、策略下发
- 可观测性
  - [ ] 监控告警(JMX、Spring Boot Actuator)
  - [ ] 调用链路（OpenTracing）
