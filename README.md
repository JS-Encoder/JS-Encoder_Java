

## 项目介绍

👉JS-encoder 是一个前端代码在线编辑分享平台 👈

该项目采用了前后端分离的技术，而本项目为后端部分

## 项目展示地址

https://www.lliiooiill.cn/

## 项目特点

-  实时显示效果。
-  支持多种前端语言及预处理语言。
-  支持外部脚本和样式。
-  本地存储, 你可以在本地保存编译器上的代码, 目前支持三种形式：
  - 单文件: 将 HTML, CSS, 和 JS 整合到一个名为 index.html 的文件中。
  - 压缩包: 将 HTML, CSS, 和 JS 分别创建一个文件并放在一个文件夹中压缩为 zip 文件。
  - 预处理文件: 如果你使用了预处理语言，那么可以下载未编译的预处理语言文件。
-  文件导入, 你可以从本地导入文件到编译器中, 成功后编译器将自动读取文件内容并显示在编辑窗口中。
-  多快捷键支持, 包括 emmet 扩展。
-  加入 markdown 模式, 支持编辑窗口和预览窗口的实时预览和同步滚动，并加入快捷菜单栏
-  代码搜索功能。
-  CDN 搜索功能，更快捷的添加所需的 CSS 和 JS 外部链接。
-  控制台，显示日志并可进行调试。
-  代码智能提示。
-  个性化设置。
-  为部分语言提供语法检查(linter)

### 项目技术栈

**后端技术栈**

1. springboot
2. springbootSecurity
3. dubbo+zookeeper
4. Mybatis-plus
5. Swagger
6. Redis
7. Mysql
8. RabbitMq

**前端开源地址**

https://github.com/JS-Encoder/JS-Encoder-Online

## 项目效果图

进入页面首页的展示

![image](http://ownlzq.firstbird.asia/JS-encoder%E9%A6%96%E9%A1%B5.png)

探索页面，用户可以在该页面上搜索到他人发布的作品和自己发布的作品

![image](http://ownlzq.firstbird.asia/JS-encoder%E6%8E%A2%E7%B4%A2%E9%A1%B5%E9%9D%A2.png)

内容详细页面，用户可以点击查看到作品中的详细代码和想过展示

![images](http://ownlzq.firstbird.asia/JS-encoder%E8%AF%A6%E7%BB%86%E9%A1%B5.png)

用户登录注册页面，注册账号需使用邮箱注册，通过邮箱验证进行账号的注册

![images](http://ownlzq.firstbird.asia/JS-encoder%E6%B3%A8%E5%86%8C%E9%A1%B5%E9%9D%A2.png)

用户登录包括账号密码登录和第三方授权登录，用户注册完账号后可自行进行==github/gitee==第三方的绑定，绑定完即可直接使用第三方授权登录。

![images](http://ownlzq.firstbird.asia/JS-encoder%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.png)

用户登录完可以进入到个人页面查看个人作品、管理个人作品或发布个人作品

![images](http://ownlzq.firstbird.asia/JS-encoder%E4%B8%AA%E4%BA%BA%E4%B8%BB%E9%A1%B5.png)

## 快速部署

该项目需要用到redis，rabbitmq，zookeeper等技术，用户需要提前安装好所需的环境搭配本地

1. clone项目到本地 https://github.com/JS-Encoder/JS-Encoder_Java.git

2. 创建好一个空的数据库 onlineide ,导入 onlineide.sql ,并修改项目中关于数据库的配置（ js-encoder-web 中 resource 目录下的 applicaition-dev.yml 文件）即可

3. 提前安装好 Redis ,在项目中的 js-encoder-web 中 resource 目录下的 applicaition-dev.yml 文件中将 Redis 配置为自己的ip

4. 提前准备好 RabbitMq ,在项目中的 js-encoder-web 中 resource 目录下的 applicaition-dev.yml 文件中将 RabbitMq 配置为自己的ip

5. 需要在 js-encoder-service 和 js-encoder-web 的 application-dev.yml 中配置好自己的 zookeeper  ip地址

6. 用户可以根据自身条件对邮箱进行配置（js-encoder-service 中 resource 目录下的 applicaition.yml ）

7. 用户需要准备一个七牛云仓库并进行配置（js-encoder-web 中 resource 目录下的 applicaition-dev.yml ）

8. 用户想要使用第三方登录功能，需要到

    js-encoder-service/src/main/java/com/lzq/dubboservice/service/oauth/GiteeOauth  和 GithubOauth中进行配置的修改，配置信息可参考 gitee 和 github 的 openApi 手册，这里以gitee为例 https://gitee.com/api/v5/oauth_doc#/

9. 该项目需要用户安装 chrome 版本相对于的 chromedriver 插件到本地中，用户需要根据本地的chrome版本到  **http://npm.taobao.org/mirrors/chromedriver** 下载对应的 chromedriver 插件，到 js-encoder-web 中 resource 目录下的 applicaition-dev.yml 中修改chromedriver 对应的路劲即可

10. 在 IntelliJ IDEA 中打开项目，启动 js-encoder-service 和 js-encoder-web 模块即可，启动完后可以通过 http://localhost:8090/swagger-ui/ 来访问swagger

**OK，至此，项目就部署成功了**



## 注意事项

1. 该项目使用到了 chromedriver 进行页面截图，而该插件需要跟本地的 chrome 版本一致 ，不然再调用时会报错

2. 需要注意 zookeeper 中 curator 包 和 seleniumhq 包中的 guava 版本，以防止包冲突导致截图过程中报**NoSuchMethod**错误

   解决办法，导入匹配的 seleniumhq 匹配的 guava 版本

   ```xml
   <dependency>
       <groupId>com.google.guava</groupId>
       <artifactId>guava</artifactId>
       <version>23.6-jre</version>
   </dependency>
   ```

   

