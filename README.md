本项目旨在提供一个方便操作Kafka的工具：

- 通过REST API方式，提供topic管理、消费者组管理、消息管理等功能
- 有余力的话，基于前述REST API提供的能力，做个前端
- `scripts/`：提供一些便捷操作kafka的命令，是对`kafka/bin`目录下的脚本的封装，详见[./scripts/README.md](./scripts)

## 功能

### 通用功能

- 查看broker信息

- topic管理

    - 增删改查，增删改可批量
    - 查看每个分区的endOffset

- 消息headers展示

- 序列化

    -
    一个集群里面存在不同的序列化方式的消息，处理消息时，怎么知道使用哪个序列化？支持在配置文件中配置多种序列化方式，其中一个为primary，类似多个数据源的配置。一个topic里面的消息只能使用一种序列化方式。topic与序列化之间映射关系可以配置：

    ```properties
    # _default_表示默认所有topic都用这个反序列化
    cn.whu.wy.kafkamate.deserializer[0].class=org.apache.kafka.common.serialization.StringDeserializer
    cn.whu.wy.kafkamate.deserializer[0].topics=_default_
    
    # 这两个topic不使用默认反序列化，使用这里指定的
    cn.whu.wy.kafkamate.deserializer[1].class=xxx.xx.xx
    cn.whu.wy.kafkamate.deserializer[1].topics=topicA,topicB
    ```

    - 有些序列化需要消息包，提供`java -Dloader.path`方案

- 解决了序列化，就可以查看消息了

    - 指定tpo三元组，显示一个消息
    - 指定消息tpo+n，显示n个消息

- 消费进度

    - 消费者组详情
    - 消费进度查看
    - 消费进度修改？
    - 未提交事务检测

- 消息删除。支持删除小于某个offset的全部消息

    - 根据时间戳删除
    - 根据offset删除

### 定制功能

- 索引
    - 指定topic以创建索引，根据配置得知topic使用什么反序列化
    - 如何指定索引字段：
        - 使用注解在消息类上进行标注，属于元数据。
        - 在消息头指定。每个消息的消息头都带这个信息，浪费资源
- 全链路
- 消息编辑。以嵌套json展示，点击可以修改，然后二次发布