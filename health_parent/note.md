1.导入excel数据时，日期格式总是导入数据库少一天，excel中的日期为2019/12/6，导入数据库变成2019-12-05，不知道是什么问题？

2.Caused by: com.alibaba.dubbo.rpc.RpcException: Fail to start server(url: dubbo://192.168.2.101:20887/com.cy.service.CheckGroupService?anyhost=false&application=health_service_provider&bind.ip=192.168.2.101&bind.port=20887&channel.readonly.sent=true&codec=dubbo&dubbo=2.6.0&generic=false&heartbeat=60000&interface=com.cy.service.CheckGroupService&methods=add,pageQuery,findCheckItemIdsByCheckGroupId,findById,deleteById,update,findAll&pid=69968&revision=1.0-SNAPSHOT&side=provider&timestamp=1575728225869) Failed to bind NettyServer on /192.168.2.101:20887, cause: Failed to bind to: /192.168.2.101:20887
这个错误可能就是，service工程绑定的本机ip地址出错，修改发布服务的ip地址

3.每个模块代表的功能
health_backend：后台
health_common：通用类，实体类定义
health_interface：接口定义
health_service_provider：服务
health_jobs：定时任务
health_mobile：移动端开发，微信公众号

4.同融健康系统

5.运营数据展示，和运营数据基于浏览器导出成excel文件。导出数据的思想是，在工程中添加一个运营数据的excel模版，然后将运营数据导入到模版中，然后基于浏览器下载文件。

