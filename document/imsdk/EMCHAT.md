2019年7月6日11:37:46
# 环信聊天文档
## 大纲
>- [环信聊天介绍](#IMClient介绍)
>    1. [环信IM初始化]
         1.1 [IM初始化配置]
         1.2 [IM初始化注册监听]
>    2. [环信IM登录](#IM登录)
>    3. [环信IM登出](#IM登出)
>    4. [环信IM消息事件]
>    5. [EMMessage介绍]

### 说明
- TS+已使用环信聊天替换老的版本的im 聊天
- 环信聊天Android集成相关介绍在此不多做描述，请参照环信提供的集成文档
- [文档地址](http://docs-im.easemob.com/im/android/sdk/import)

## 环信聊天介绍

### 1 IM初始化
    - 在AppApplication下，对环信进行初始化
        private void initIm() {
            TSEMHyphenate.getInstance().initHyphenate(this);
        }
#### 1.1 IM初始化配置
    - 具体的初始化流程都在TSEMHyphenate的initHyphenate方法中，以下为具体的初始化操作:
    /**
     * 初始化环信的SDK
     * @param context 上下文菜单
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean initHyphenate(Context context) {
        mContext = context;
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此 application 的 onCreate 是被service 调用的，直接返回
            return true;
        }
        if (isInit) {
            return isInit;
        }
        mContext = context;

        // 调用初始化方法初始化sdk
        EaseUI.getInstance().init(mContext, initOptions());

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(BuildConfig.USE_DOMAIN_SWITCH);

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        return isInit;
    }

    - 初始化initOption().关于环信聊天的一些配置信息设置
    private EMOptions initOptions() {
       /**
        * SDK初始化的一些配置
        * 关于 EMOptions 可以参考官方的 API 文档
        * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
        */
       EMOptions options = new EMOptions();
       // 启动私有化配置
       options.enableDNSConfig(true);

       // 设置Appkey，如果配置文件已经配置，这里可以不用设置
       // options.setAppKey("lzan13#hxsdkdemo");

       // 设置自动登录
       options.setAutoLogin(true);

       // 设置是否按照服务器时间排序，false按照本地时间排序
       options.setSortMessageByServerTime(false);

       // 设置是否需要发送已读回执
       options.setRequireAck(true);

       // 设置是否需要发送回执
       options.setRequireDeliveryAck(true);

       // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
       options.setAcceptInvitationAlways(false);

       // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
       options.setAutoAcceptGroupInvitation(true);

       // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
       options.setDeleteMessagesAsExitGroup(false);

       // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
       options.allowChatroomOwnerLeave(true);

       // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
       options.setAutoTransferMessageAttachments(true);

       // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
       options.setAutoDownloadThumbnail(true);

       // 设置google GCM推送id，国内可以不用设置
       options.setUseFCM(true);
       options.setFCMNumber(TSEMConstants.ML_GCM_NUMBER);
       // 设置集成小米推送的appid和appkey
       options.setMipushConfig(TSEMConstants.ML_MI_APP_ID, TSEMConstants.ML_MI_APP_KEY);
       // TODO 主动调用华为官方的注册华为推送 测试用，SDK内部已经调用
       // PushManager.requestToken(mContext);
       return options;
   }
#### 1.2 IM初始化注册监听
     1.2.1 注册消息监听
     1.2.2 注册联系人监听
     1.2.3 注册通话监听
     1.2.4 注册连接监听
     1.2.5 群组变化监听
     1.2.6 通话广播监听
    - 初始化全局监听，具体的相关监听器种类，及监听器内部实现的逻辑，请前往代码查看
   /**
    * @Description 初始化全局监听
    * 连接监听 {@link #setConnectionListener()}
    * 消息监听 {@link #setMessageListener()}
    * 联系人监听 {@link #setContactListener()}
    * 群组监听 {@link #setGroupChangeListener()}
    */
   private void initGlobalListener() {

       // 设置通话广播监听
       setCallReceiverListener();

       // 通话状态监听，TODO 这里不直接调用，只需要在有通话时调用
       // setCallStateChangeListener();

       // 设置全局的连接监听
       setConnectionListener();

       // 初始化全局消息监听
       setMessageListener();

       // 设置全局的联系人变化监听
       setContactListener();

       // 设置全局的群组变化监听
       setGroupChangeListener();

   }

    1.注册消息监听，用来监听全局消息接收等
    EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    EMMessageListener  mMessageListener = new EMMessageListener() {
            /**
             * @param list 收到的新消息集合，离线和在线都是走这个监听
             */
            @Override
            public void onMessageReceived(List<EMMessage> list) { }
            /**
             * @param list 收到的透传消息集合
             */
            @Override
            public void onCmdMessageReceived(List<EMMessage> list) { }
            /**
             * @param list 收到消息已读回执
             */
            @Override
            public void onMessageRead(List<EMMessage> list) { }

            /**
             * @param list 收到发送回执的消息集合
             */
            @Override
            public void onMessageDelivered(List<EMMessage> list) { }

            @Override
            public void onMessageRecalled(List<EMMessage> list) { }
            /**
             * @param message 发生改变的消息
             * @param object  包含改变的消息
             */
            @Override
            public void onMessageChanged(EMMessage message, Object object) { }
        };
    }
    2.注册联系人监听，用来监听全局联系人的请求与变化等
    EMClient.getInstance().contactManager().setContactListener(mContactListener);
    EMContactListener mContactListener = new EMContactListener() {
        /**
         * @param username 被添加的联系人
         */
        @Override
        public void onContactAdded(String username) { }
        /**
         * @param username 删除的联系人
         */
        @Override
        public void onContactDeleted(String username) { }
        /**
         * 收到对方联系人申请
         * @param username 发送好友申请者username
         * @param reason 申请理由
         */
        @Override
        public void onContactInvited(String username, String reason) { }
        /**
         * 对方同意了自己的申请
         * @param username 对方的username
         */
        @Override
        public void onFriendRequestAccepted(String username) { }
        /**
         * 对方拒绝了联系人申请
         * @param username 对方的username
         */
        @Override
        public void onFriendRequestDeclined(String username) { }
    };
    3.注册通话监听，用来监听通话状态变化等
    EMClient.getInstance().callManager().addCallStateChangeListener(mCallStateListener);
    EMCallStateChangeListener mCallStateListener = new EMCallStateChangeListener() {
        /**
         *通话状态变化，状态，以及错误信息
         */
        @Override
        public void onCallStateChanged(CallState callState, CallError callError) {

        }
    };

    4.注册连接监听，用来监听与聊天服务器的链接情况变化等
    EMClient.getInstance().addConnectionListener(mConnectionListener);
    EMConnectionListener mConnectionListener = new EMConnectionListener() {
            /**
             * 连接聊天服务器成功
             */
            @Override
            public void onConnected() { }
            /**
             * 链接聊天服务器失败
             * @param errorCode 连接失败错误码
             */
            @Override
            public void onDisconnected(final int errorCode) { }
        };
    5.群组变化监听，用来监听群组请求，以及其他群组情况
    EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener);
    EMGroupChangeListener mGroupChangeListener = new EMGroupChangeListener() {
        /**
         * 收到其他用户邀请加入群组
         * @param groupId   要加入的群的id
         * @param groupName 要加入的群的名称
         * @param inviter   邀请者
         * @param reason    邀请理由
         */
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) { }
        /**
         * 用户申请加入群组
         * @param groupId   要加入的群的id
         * @param groupName 要加入的群的名称
         * @param applyer   申请人的username
         * @param reason    申请加入的reason
         */
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) { }

        /**
         * 加群申请被对方接受
         *
         * @param groupId 申请加入的群组id
         * @param groupName 申请加入的群组名称
         * @param accepter 同意申请的用户名（一般就是群主）
         */
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) { }

        /**
         * 加群申请被拒绝
         *
         * @param groupId 申请加入的群组id
         * @param groupName 申请加入的群组名称
         * @param decliner 拒绝者的用户名（一般就是群主）
         * @param reason 拒绝理由
         */
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) { }

        /**
         * 对方接受群组邀请
         *
         * @param groupId 邀请对方加入的群组
         * @param invitee 被邀请者
         * @param reason 理由
         */
        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) { }

        /**
         * 对方拒绝群组邀请
         * @param groupId 邀请对方加入的群组
         * @param invitee 被邀请的人（拒绝群组邀请的人）
         * @param reason 拒绝理由
         */
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) { }

        /**
         * 当前登录用户被管理员移除出群组
         *
         * @param groupId 被移出的群组id
         * @param groupName 被移出的群组名称
         */
        @Override
        public void onUserRemoved(String groupId, String groupName) { }

        /**
         * 群组被解散。 sdk 会先删除本地的这个群组，之后通过此回调通知应用，此群组被删除了
         *
         * @param groupId 解散的群组id
         * @param groupName 解散的群组名称
         */
        @Override
        public void onGroupDestroyed(String groupId, String groupName) { }

        /**
         * 自动同意加入群组 sdk会先加入这个群组，并通过此回调通知应用
         *
         * @param groupId 收到邀请加入的群组id
         * @param inviter 邀请者
         * @param inviteMessage 邀请信息
         */
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) { }

    };
    6.通话广播监听
    // 设置通话广播监听器过滤内容
    IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
    if (mCallReceiver == null) {
        mCallReceiver = new TSEMCallReceiver();
    }
    //注册通话广播接收者
    mContext.registerReceiver(mCallReceiver, callFilter);

### 2 环信IM登录
    EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
        @Override
        public void onSuccess() {
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
        }
        @Override
        public void onProgress(int progress, String status) {

        }
        @Override
        public void onError(int code, String message) {
            Log.d("main", "登录聊天服务器失败！");
        }
    });

### 3 环信IM登出
    /**
     * 退出登录环信
     * @param callback 退出登录的回调函数，用来给上次回调退出状态
     */
    public void signOut(final EMCallBack callback) {
        /**
         * 调用sdk的退出登录方法，此方法需要两个参数
         * boolean 第一个是必须的，表示要解绑Token，如果离线状态这个参数要设置为false
         * callback 可选参数，用来接收推出的登录的结果
         */
        EMClient.getInstance().logout(isUnbuildToken, new EMCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

### 4.环信IM消息事件
    4.1 发送文本消息
    4.2 发送语音消息
    4.3 发送图片消息
    4.4 发送地理位置消息
    4.5 发送透传消息
    4.6 监听消息状态
    4.7 获取聊天记录
    4.8 获取未读消息数量
    4.9 未读消息数清零
    4.10 获取消息总数
    发送文本、语音、图片、位置等消息（单聊/群聊通用）。
    1.发送文本消息
    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
    EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
    //如果是群聊，设置chattype，默认是单聊
    if (chatType == CHATTYPE_GROUP)
        message.setChatType(ChatType.GroupChat);
    //发送消息
    EMClient.getInstance().chatManager().sendMessage(message);

    2.发送语音消息
    //filePath为语音文件路径，length为录音时间(秒)
    EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
    //如果是群聊，设置chattype，默认是单聊
    if (chatType == CHATTYPE_GROUP)
        message.setChatType(ChatType.GroupChat);
    EMClient.getInstance().chatManager().sendMessage(message);

    3.发送图片消息
    //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
    EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
    //如果是群聊，设置chattype，默认是单聊
    if (chatType == CHATTYPE_GROUP)
        message.setChatType(ChatType.GroupChat);
    EMClient.getInstance().chatManager().sendMessage(message);

    4.发送地理位置消息
    //latitude为纬度，longitude为经度，locationAddress为具体位置内容
    EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
    //如果是群聊，设置chattype，默认是单聊
    if (chatType == CHATTYPE_GROUP)
        message.setChatType(ChatType.GroupChat);
    EMClient.getInstance().chatManager().sendMessage(message);

    5.发送透传消息
    透传消息能做什么：头像、昵称的更新等。可以把透传消息理解为一条指令，通过发送这条指令给对方，告诉对方要做的 action，收到消息可以自定义处理的一种消息。（透传消息不会存入本地数据库中，所以在 UI 上是不会显示的）。另，以“em_”和“easemob::”开头的action为内部保留字段，注意不要使用

    EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
    //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
    cmdMsg.setChatType(ChatType.GroupChat)
    String action="action1";//action可以自定义
    EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
    String toUsername = "test1";//发送给某个人
    cmdMsg.setTo(toUsername);
    cmdMsg.addBody(cmdBody);
    EMClient.getInstance().chatManager().sendMessage(cmdMsg);

    6.监听消息状态
    通过 message 设置消息的发送及接收状态。

    message.setMessageStatusCallback(new EMCallBack(){});
    7.获取聊天记录
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
    //获取此会话的所有消息
    List<EMMessage> messages = conversation.getAllMessages();
    //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
    //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
    List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
    8.获取未读消息数量
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
    conversation.getUnreadMsgCount();
    9.未读消息数清零
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
    //指定会话消息未读数清零
    conversation.markAllMessagesAsRead();
    //把一条消息置为已读
    conversation.markMessageAsRead(messageId);
    //所有未读消息数清零
    EMClient.getInstance().chatManager().markAllConversationsAsRead();
    10.获取消息总数
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
    //获取此会话在本地的所有的消息数量
    conversation.getAllMsgCount();
    //如果只是获取当前在内存的消息数量，调用
    conversation.getAllMessages().size();

### 5.EMMessage介绍
    1.消息类型
    2.消息方向类型
    3.消息发送状态
    4.聊天类型
    5.MessageBody消息体介紹
    6.环信相关的EventBusTag 介绍
    7.TSEMConstants介绍
    1.消息类型
    /**
     * 消息类型：文本，图片，视频，位置，语音，透传消息
    public enum Type {
        TXT, IMAGE, VIDEO, LOCATION, VOICE,CMD
    }
    2.消息方向类型
   /**
     * 消息的方向类型：区分是发送消息还是接收到的消息
     *
    public enum Direct {
        SEND, RECEIVE
    }
    3.消息发送状态
    /**
     * 消息的发送/接收状态：成功，失败，发送/接收过程中，创建成功待发送
     *
    public enum Status {
        SUCCESS, FAIL, INPROGRESS, CREATE
    }
    4.聊天类型
    /**
     * 聊天类型：单聊，群聊，聊天室
     *
    public enum ChatType {
        /**
         * 单聊
         */
        Chat,
        /**
         * 群聊
         */
        GroupChat,
        /**
         * 聊天室
         */
        ChatRoom
    }
    5.MessageBody消息体介紹
    EMTextMessageBody  文本消息体
    EMVoiceMessageBody  语音消息体
    EMImageMessageBody  图片消息体
    EMVideoMessageBody  视频消息体
    EMLocationMessageBody  位置消息体
    EMNormalFileMessageBody  文件消息体

    6.环信相关的EventBusTag 介绍(部分已废弃，请参照代码区分)
    public static final String EVENT_IM_ONMESSAGERECEIVED = "onMessageReceived";
    public static final String EVENT_IM_ONMESSAGEACKRECEIVED = "onMessageACKReceived";
    //聊天服务器连接成功
    public static final String EVENT_IM_ONCONNECTED = "onConnected";
    //聊天加载成功
    public static final String EVENT_IM_AUTHSUCESSED = "onauthSucessed";
    //聊天服务器连接断开
    public static final String EVENT_IM_ONDISCONNECT = "onDisconnect";
    //聊天错误信息
    public static final String EVENT_IM_ONERROR = "onError";
    //超时
    public static final String EVENT_IM_ONMESSAGETIMEOUT = "onMessageTimeout";
    //新对话创建回调
    public static final String EVENT_IM_ONCONVERSATIONCRATED = "onConversationCrated";
    public static final String EVENT_IM_ONMESSAGERECEIVED_V2 = "onMessageReceivedV2";

    7.TSEMConstants介绍
    public class TSEMConstants {

        // GCM number
        public static final String ML_GCM_NUMBER = "700117461212";

        // 集成小米推送需要的 appid 和 appkey
        public static final String ML_MI_APP_ID = "2882303761517946480";
        public static final String ML_MI_APP_KEY = "5961794679480";

        // 华为推送 APPID
        public static final String ML_HUAWEI_APP_ID = "100623125";

        // TalkingData统计平台 APPID
        public static final String TD_APP_ID = "6227388DE1332BBBF47E55EB85290B58";
        ...
        ...
    TSEMConstants类中涵盖了Ts+环信聊天里所用到的常量信息。具体内容自行咋代码中查看
