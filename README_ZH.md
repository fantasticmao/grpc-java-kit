# gRPC Java Kit

[![Actions Status](https://github.com/fantasticmao/grpc-java-kit/workflows/ci/badge.svg)](https://github.com/fantasticmao/grpc-java-kit/actions)
[![Maven Central](https://img.shields.io/maven-central/v/cn.fantasticmao.grpc-kit/grpc-kit-all.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cn.fantasticmao.grpc-kit%22)
[![image](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/fantasticmao/grpc-java-kit/blob/main/LICENSE)

## 依赖关系

```text
+-----------------+   +-----------------+
| grpc-kit-client |   | grpc-kit-server |
+-----------------+   +-----------------+

+-----------------------+   +-----------------------+   +------------------+
| grpc-kit-nameresolver |   | grpc-kit-loadbalancer |   | grpc-kit-monitor |
+-----------------------+   +-----------------------+   +------------------+

+-----------------+
| grpc-kit-common |
+-----------------+
```

## 特性列表

### RPC 通信

- [x] 原生 grpc-java

### 服务治理

- 服务注册、服务发现
    - [ ] 多租户
    - [ ] ZooKeeper、Consul
- 负载均衡、流量管理
    - [ ] 轮训、随机、加权随机
    - [ ] 流量着色
    - [ ] 超时重试、限流熔断、服务降级
    - [ ] 策略下发
- 可观测性
    - [ ] 监控告警(Grafana + Webhook)
    - [ ] 调用链路（OpenTracing）
    - [ ] gRPC 调用的吞吐、延迟、状态码分布
