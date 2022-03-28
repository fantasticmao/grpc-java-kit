# gRPC Java Kit

[![Actions Status](https://github.com/fantasticmao/grpc-java-kit/workflows/ci/badge.svg)](https://github.com/fantasticmao/grpc-java-kit/actions)
![JDK Version](https://img.shields.io/badge/JDK-11%2B-blue)
[![Maven Central](https://img.shields.io/maven-central/v/cn.fantasticmao.grpc-kit/grpc-kit-all.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cn.fantasticmao.grpc-kit%22)
[![image](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/fantasticmao/grpc-java-kit/blob/main/LICENSE)

## 特性列表

### RPC 通信

- [x] 原生 [gRPC-Java](https://github.com/grpc/grpc-java)

### 服务治理

- 服务注册、服务发现
  - [x] 分组
  - [x] ZooKeeper、~~Consul~~
- 负载均衡、流量管理
  - [x] 加权随机、~~加权轮训~~、~~加权最少连接数量~~、~~加权最少平均耗时~~
  - [ ] 流量染色
  - [ ] 认证授权
- 容错设计
  - [ ] 超时重试、快速失败
  - [ ] 限流、熔断、降级
  - [ ] 故障检测、策略下发
- 可观测性
  - [ ] 监控告警(Grafana + Prometheus)
  - [ ] 调用链路（OpenTracing）
