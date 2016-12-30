package cn.edu.bjtu.weibo.service.serviceImpl;

import cn.edu.bjtu.weibo.dao.UserDAO;
import cn.edu.bjtu.weibo.dao.daoImpl.UserDaoImpl;
import cn.edu.bjtu.weibo.model.Comment;
import cn.edu.bjtu.weibo.model.Weibo;
import cn.edu.bjtu.weibo.service.FilterBanedKeywordsService;
import cn.edu.bjtu.weibo.service.SendContentSevice;

public class SendContentServiceImpl implements SendContentSevice{

	//��ȡ�û����ǿ� ���ݿո��жϽ�β��������㻹û�����ôŪ
	//package cn.edu.bjtu.weibo.service.impl;

		@Override
		public boolean sendWeibo(String userId, Weibo weibo) {
			String weiboContent=weibo.getContent();
			String atContent,topicContent,textContent = null,username,topic;
			String[] textContentArr;
			if(weiboContent.contains("@")){
				int atIndex=weiboContent.indexOf("@");//�˴�@�û����ո��������������ûŪ������������
				String temp=weiboContent.substring(0,atIndex);
				String tempnext=weiboContent.substring(atIndex);
				int spaceIndex=tempnext.indexOf(" ");
				username=tempnext.substring(1,spaceIndex);
				atContent="<a href=???><font color=red>@"+username+"</font></a>";//����ɫ�������ұ����Ժ��ѯ
				tempnext=tempnext.substring(spaceIndex+1);
				
				//
				textContentArr=weiboContent.split("@"+username);
				textContent=textContentArr[0]+textContentArr[1];
				temp=temp+atContent+tempnext;
				weiboContent=temp;
				
				//System.out.println(tempnext);
			}
			if(weiboContent.contains("#")){
				int topstartIndex=weiboContent.indexOf("#");//�˴�#����,#�Ž���
				String temp=weiboContent.substring(0,topstartIndex);
				String tempnext=weiboContent.substring(topstartIndex);
				int topendIndex=tempnext.lastIndexOf("#");
				topic=tempnext.substring(1,topendIndex);
				topicContent="<a href=???><font color=red>#"+topic+"#</font></a>";//����ɫ�������ұ����Ժ��ѯ
				tempnext=tempnext.substring(topendIndex+1);
				//
				textContentArr=textContent.split("#"+topic+"#");
				textContent=textContentArr[0]+textContentArr[1];
				temp=temp+topicContent+tempnext;
				weiboContent=temp;
				
			}
			//���дʼ��,��û�У�������ݿ⣬���򣬲�����
			//if(textContent)
			//�û���ѯ�����У���Ҫ���û����ѣ����������ݿ�
			//if(username)
			//�����ѯ�����У����뻰������ݿ⣬��û�У���������
			//if(topic)
			FilterBanedKeywordsService detect=new FilterBanedKeywordsServiceImpl();
			if(detect.isBanedKeywordInside(weiboContent)){
				//��ǰ�˷��ز�����
				System.out.println("ǰ����ʾ�����ԣ���");
				return false;
			}else{
				UserDAO userDao=new UserDaoImpl();
				
				System.out.println(weiboContent);
				
				return true;
			}
			
		}

		@Override
		public boolean sendComment(String userId, Comment comment) {
			// TODO Auto-generated method stub
			return false;
		}


}
