# 基于RxJava2 + Retrofit2 + Greendao实现文件下载：支持多文件、多线程断点续传
在开发中，经常遇到文件下载，版本更新等功能。花了几天时间用RxJava2、Retrofit2 实现了一个文件下载库。

主要实现了
1. 文件的下载、暂停、删除、恢复下载功能
2. 多文件的全部开始、全部暂停、全部删除下载任务功能
3. 支持自定义同时进行下载的最大线程数量：默认3个
4. 支持自定义本地下载路径
5. 支持断点续传：库中实现了数据库结构，用于保存每条下载纪录的信息
6. 封装动态权限帮助类：可直接使用请求相关权限

## 使用方式
### 第一步. 添加依赖:
```Java
compile 'com.mickywu:filedownlibrary:1.0.0'
```
### 第二步. 在Application的onCreate()方法中初始化:
```Java
FileDown.init(this);
```
### 第三步. 实例化DownloadHelper：
```Java
 mHelper = DownloadHelper.getInstance();
 ```
 
如果你需要下载状态的相关信息，还需要在页面上注册监听(推荐在onStart()注册)：
```Java
 mHelper.registerListener(this);
 ```
如果你注册了监听，页面销毁的时候，一定要解除监听（在onStop()解除）
```Java
mHelper.unRegisterListener();
```
实现相关接口方法：
```Java
 /**
     * 开始下载
     * @param url
     */
    void start(String url);

    /**
     * 暂停下载
     * @param url
     */
    void pause(String url);

    /**
     * 下载进度
     * @param read   已下载大小
     * @param contentLength   总大小
     * @param done 是否完成
     * @param url 下载连接
     * @param kbs 下载速度 kb/s
     */
    void progress(long read, long contentLength, boolean done, String url,long kbs);

    /**
     * 等待下载
     * @param url 下载连接
     */
    void wait(String url);

    /**
     * 下载完成
     * @param url 下载连接
     */
    void complete(String url, File file);

    /**
     *  下载完成
     * @param url
     */
    void error(String url, String msg);

    /**
     *  删除任务
     * @param url
     */
    void delete(String url);
```
### 第四步. 开始下载
开始下载：
```Java
mHelper.start(url);
```
暂停下载：
```Java
mHelper.pause(url);
```
恢复下载（暂停状态恢复下载状态）：
```Java
 mHelper.resume(url);
 ```
 删除下载：
 ```Java
 mHelper.delete(url);
 ```
 ### 第五步. 其他设置
 设置最大线程数：
 ```Java
 mHelper.setMaxTask(4);
 ```
 设置文件保存路径：
 ```Java
 mHelper.setSavePath(mSaveFilePath);
 ```
 
 ### 注意： Android6.0以后的系统文件开始下载时一定要检测是否开启了动态权限，文件读写权限开启后方能开始下载。本库内置了动态权限请求库：
 1. 初始化并发起权限申请
 ```Java
  
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
 ```
 2. 实现如下接口：
 ```Java
 
    @Override
    public int getPermissionsRequestCode() {
        // //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        ///设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
    }

    @Override
    public void requestPermissionsSuccess() {
       // 请求成后方可以下载，这里没有处理相关逻辑
    }

    @Override
    public void requestPermissionsFail() {

    }
  ```
  3. 在Activity中实现onRequestPermissionsResult回掉
  ```Java
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
 ```
    
 ### 主要方法
 |方法名|作用|
 |--|--|
 |getInstance()|静态方法，以单列方式获取下载帮助类对象|
 |setSavePath(String localPath)|设置文件保存路径|
 |setMaxTask(int count)|设置最大下载进程数（同时可以下载的最大数量，默认三个）|
 |registerListener(DownloadListener listener)|注册监听，回掉当前下载状态|
 |unRegisterListener()|解除监听，与registerListener配对使用，页面销毁时调用，防止内存泄漏|
 |start(String url)|开始单个文件下载|
 |start(List<String> list)|开始多个下载任务|
 |pause(String url)|暂停单个下载任务|
 |pauseAll()|暂停所有进行中的任务|
 |resume(String url)|恢复下载任务（暂停状态恢复下载）|
 |delete(String url)|删除下载任务|
 |deleteAll()|删除所有下载任务|
 |getLocalFilePathFromUrl(String url)|根据下载链接获取本地存储路径|
 
 ### 其他工具类
 SpeedUtils：
 
 |方法名|作用|
 |--|--|
 |FormetFileSize(long file)|long类型的下载速度转换为字符串“b/s、kb/s、m/s、g/s”|
 
 
