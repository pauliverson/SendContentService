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
		 
		List valid_topic_index  = new  ArrayList();   //�Ϸ���#
		List topic_index = new ArrayList();  //ȫ����#
		 
	     //�Ƚ���# #,ʶ�����е�#����¼λ��
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 List topic = new ArrayList();   //һ���Ϸ��Ļ���
		 List left_char =new ArrayList();   //��#��index
		 List right_char =new ArrayList();   //��#��index
		
		 if(topic_index.isEmpty()==false){   //�л���
			 
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //����#���ţ���ɾȥ��� ���ұ߱���µ�left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //һ�����ⱻ�ҳ�����
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //����@ɨ��
				   
				   
				   left_char.add(left);
				   right_char.add(right);
				   
				   String topic_str = content.substring(left+1, right);
				   topic.add(topic_str);	
				   
				   left=-1;
			 }
			 
		 }
		
	}
		 
		 
		
	    //ɨ�� @  �������Ϸ�#  # ���Զ�����
	    int j=0;
	    int skip_index =-1;
	    
	    if(valid_topic_index.isEmpty()==false){       //�л���
	                skip_index = (int) valid_topic_index.get(j);
	    }else{
	    	skip_index=-1;  //����Ҫ�κ�����
	    }
	    
	    
	    List at = new ArrayList();   //���@������
	    List at_start_index = new ArrayList();  //@�Ŀ�ʼ
	    List at_end_index = new ArrayList();    //@�Ľ���
	    
	
	    List at_index = new ArrayList();        //ȫ���Ϸ���@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //�ַ������һ����@  �Զ�����
	    	
	    	if(index==skip_index){    //����ط��ǻ���,������������
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //��һ����#
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
	    			)){                                      //����� @+����  @@ @# ������
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    if(at_index.isEmpty()==false){   //@����Ϊ��
	    	
	    	
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //�������� ��Ҫ����
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
	    	//�Ϸ����ַ� ������
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

	    //ƴ���µ�content 
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
	    
	     String weiboId =weiboDAO.insertNewWeibo(weibo);  //������΢���������ݿ�
	     
	    
	  
	   
	    userDAO.insertWeibo(userId, weiboId); // �ҷ������΢��
	     
	     
	    /*
	     * 
	     * 
	     * ����������΢����Ļ���� @ ���д���
	     */
	    
	    //�Ի���Ĵ���
		  List<String>  topic_list = new ArrayList<String>();  //���е�topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //���е�topicID
			   
		    
		 //�õ����л��������
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  if(topic_name_list.contains(topic.get(i))){
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertWeibo(topic_list.get(index), weiboId);  //�û����΢�� 
				  
				  System.out.println("��������Ⲣ������΢����"+topic.get(i));
				  
			  }else{    //����һ������
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				 String topicId= topicDAO.insertNewTopic(new_topic);
				 topicDAO.insertWeibo(topicId, weiboId);
				  
				 System.out.println("�½��˻��Ⲣ������΢����"+topic.get(i));
			  }
		  }
		    
		  //��@�Ĵ���
		  List<String>  user_list = new ArrayList<String>();  //���е�userID
		  List<String>  user_name_list = new ArrayList<String>();  //���е�userID��name
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //����Map
		  }
		   
		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.contains(at.get(i))){    //�����û�
				  
				  int index=user_name_list.indexOf(at.get(i));
				  messageToMeService.atMeInfromWeibo(user_list.get(index), weiboId);   //�õ���@���û�ID  
				  
				  System.out.println("������û�����������Ϣ��"+at.get(i));
				  
			  }else{
				  
				 //�û������� ���ùܣ�
				  System.out.println("û����û����ùܣ�"+at.get(i));
			  }
		  }
		  
		  //�ñ����۵���֪��
		  messageToMeService.commentMyWeiboInform(userId, weiboId);
		  return true;
	}

	@Override
	public boolean sendComment(String userId, Comment comment) {
		// TODO Auto-generated method stub
		
		return false;
	}

	

}
