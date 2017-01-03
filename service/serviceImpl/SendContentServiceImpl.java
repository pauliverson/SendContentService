package cn.edu.bjtu.weibo.service.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.bjtu.weibo.dao.WeiboDAO;
import cn.edu.bjtu.weibo.model.Comment;
import cn.edu.bjtu.weibo.model.Weibo;
import cn.edu.bjtu.weibo.service.SendContentSevice;
//package cn.edu.bjtu.weibo.service.impl;

@Service("SendContentService")
public class SendContentServiceImpl implements SendContentSevice{
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CommentDAO commentDAO;
	@Autowired
	private WeiboDAO weiboDAO;	
	@Autowired
	private TopicDAO topicDAO;
	@Autowired
	private MessageToMeService messageToMeService;
	@Autowired
	private CommentMessageService commentMessageService;
	
	public boolean sendWeibo(String userId, Weibo weibo) {
		String content = weibo.getContent();
		 
		List valid_topic_index  = new  ArrayList();   //合法的#
		List topic_index = new ArrayList();  //全部的#
		 
	     //先解析# #,识别所有的#，记录位置
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 List topic = new ArrayList();   //一个合法的话题
		 List left_char =new ArrayList();   //左#的index
		 List right_char =new ArrayList();   //右#的index
		
		 if(topic_index.isEmpty()==false){   //有话题
			 
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //两个#挨着，则删去左边 ，右边变成新的left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //一个话题被找出来了
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //方便@扫描
				   
				   
				   left_char.add(left);
				   right_char.add(right);
				   
				   String topic_str = content.substring(left+1, right);
				   topic.add(topic_str);	
				   
				   left=-1;
			 }
			 
		 }
		
	}
		 
		 
		
	    //扫描 @  ，遇到合法#  # ，自动跳过
	    int j=0;
	    int skip_index =-1;
	    
	    if(valid_topic_index.isEmpty()==false){       //有话题
	                skip_index = (int) valid_topic_index.get(j);
	    }else{
	    	skip_index=-1;  //不需要任何跳过
	    }
	    
	    
	    List at = new ArrayList();   //存放@的内容
	    List at_start_index = new ArrayList();  //@的开始
	    List at_end_index = new ArrayList();    //@的结束
	    
	
	    List at_index = new ArrayList();        //全部合法的@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //字符串最后一个是@  自动过滤
	    	
	    	if(index==skip_index){    //这个地方是话题,跳过整个话题
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //下一个左#
	    		if(j<valid_topic_index.size()){
	    			skip_index=(int)valid_topic_index.get(j);
	    		}	    		
	    		
	    	}else{
	    	    char want = content.charAt(index);
	    	    char want_right = content.charAt(index+1);
	    	
	    	    if(want=='@' && (want_right!='#' && want_right!='!' && want_right!=' ' && want_right!='$' &&
	    			want_right!='%' && want_right!='^' && want_right!='&' && want_right!='*' && want_right!='(' &&
	    			want_right!=')' && want_right!='=' && want_right!='+' && want_right!='{' && want_right!='}' &&
	    			want_right!='@'
	    			)){                                      //解决了 @+结束  @@ @# 的问题
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    if(at_index.isEmpty()==false){   //@不能为空
	    	
	    	
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //遇到符号 需要结束
	    		at_end = index-1;
	    		String at_str = content.substring(at_start+1, at_end+1);
	    		at.add(at_str);
	    		at_start_index.add(at_start);
	    		at_end_index.add(at_end);
	    		
	    		k++;
	    		if(k<at_index.size()){
	    		    index = (int) at_index.get(k);
	    		}
	    		at_start=index;
	    		at_end=-1;
	    			
	    	}
	    	//合法的字符 ，跳过
	    }
	    
	    if(k<at_index.size()){
	    at_start=(int) at_index.get(k);
	    at_end = content.length()-1;
	    String at_str = content.substring(at_start+1);
	    
	    at.add(at_str);
		at_start_index.add(at_start);
		at_end_index.add(at_end);
	   
	    }
	}

	    //拼接新的content 
	    String new_content ="";
	    
	    int a=0;  //@
	    int b=0;  //#
	    for(int index=0;index<content.length();index++){
	    	
	    	char temp = content.charAt(index);
	    	String str ="";
	    	if(at_start_index.isEmpty()==false && index==(int)at_start_index.get(a)){  //@
	    		 str = "<a href = ????><font color=red>@" +at.get(a)+"</font></a>";	    		
	    		 index =(int) at_end_index.get(a);
	    		 if((a+1)<at.size()){	    		   
	    		    a++;
	    		}
	    		 
	    	}
	    	
	    	else if(left_char.isEmpty()==false && index==(int)left_char.get(b)){   //#
	    		 str = "<a href = ???><font color=blue>#" +topic.get(b)+"#</font></a>";		    		 
	    		 index=(int)right_char.get(b);
	    		 if((b+1)<topic.size()){
	    		    b++;
	    		}
	    		 	    			    	 		   	    	
	    	}
	    	
	    	else{
	    		 str = String.valueOf(temp);
	    	}
	    	
	    	new_content=new_content+str;
	    }
	    
	     System.out.println(content);
	     System.out.println(new_content);
	    
	     //comment.setCommentOrWeiboId(weiboId);
	     weibo.setLike(0);
	     
	     Date date = new Date();
	     DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String time=format.format(date);
	     weibo.setDate(time);
	     weibo.setAtUserIdList(at);
	     weibo.setTopicIdList(topic);
	     weibo.setContent(new_content);
	    
	     String weiboId =weiboDAO.insertNewWeibo(weibo);  //将此条微博插入数据库
	     
	    
	  
	   
	    userDAO.insertWeibo(userId, weiboId); // 我发表过的微博
	     
	     
	    /*
	     * 
	     * 
	     * 第三步：对微博里的话题和 @ 进行处理
	     */
	    
	    //对话题的处理
		  List<String>  topic_list = new ArrayList<String>();  //所有的topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //所有的topicID
			   
		    
		 //得到所有话题的名字
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  if(topic_name_list.contains(topic.get(i))){
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertWeibo(topic_list.get(index), weiboId);  //该话题的微博 
				  
				  System.out.println("有这个话题并插入了微博："+topic.get(i));
				  
			  }else{    //创建一个话题
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				 String topicId= topicDAO.insertNewTopic(new_topic);
				 topicDAO.insertWeibo(topicId, weiboId);
				  
				 System.out.println("新建了话题并插入了微博："+topic.get(i));
			  }
		  }
		    
		  //对@的处理
		  List<String>  user_list = new ArrayList<String>();  //所有的userID
		  List<String>  user_name_list = new ArrayList<String>();  //所有的userID和name
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //放入Map
		  }
		   
		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.contains(at.get(i))){    //存在用户
				  
				  int index=user_name_list.indexOf(at.get(i));
				  messageToMeService.atMeInfromWeibo(user_list.get(index), weiboId);   //得到被@的用户ID  
				  
				  System.out.println("有这个用户就提醒他消息："+at.get(i));
				  
			  }else{
				  
				 //用户不存在 不用管？
				  System.out.println("没这个用户不用管："+at.get(i));
			  }
		  }
		  
		  //让被评论的人知道
		  messageToMeService.commentMyWeiboInform(userId, weiboId);
		  return true;
	}

	@Override
	public boolean sendComment(String userId, Comment comment) {
		// TODO Auto-generated method stub
		
		return false;
	}

	

}
