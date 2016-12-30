package cn.edu.bjtu.weibo.service.serviceImpl;

import cn.edu.bjtu.weibo.dao.UserDAO;
import cn.edu.bjtu.weibo.dao.daoImpl.UserDaoImpl;
import cn.edu.bjtu.weibo.model.Comment;
import cn.edu.bjtu.weibo.model.Weibo;
import cn.edu.bjtu.weibo.service.FilterBanedKeywordsService;
import cn.edu.bjtu.weibo.service.SendContentSevice;

public class SendContentServiceImpl implements SendContentSevice{

	//截取用户名那块 根据空格判断结尾，其他标点还没想好怎么弄
	//package cn.edu.bjtu.weibo.service.impl;

		@Override
		public boolean sendWeibo(String userId, Weibo weibo) {
			String weiboContent=weibo.getContent();
			String atContent,topicContent,textContent = null,username,topic;
			String[] textContentArr;
			if(weiboContent.contains("@")){
				int atIndex=weiboContent.indexOf("@");//此处@用户，空格结束，标点结束还没弄出来。。。。
				String temp=weiboContent.substring(0,atIndex);
				String tempnext=weiboContent.substring(atIndex);
				int spaceIndex=tempnext.indexOf(" ");
				username=tempnext.substring(1,spaceIndex);
				atContent="<a href=???><font color=red>@"+username+"</font></a>";//做变色处理，并且便于以后查询
				tempnext=tempnext.substring(spaceIndex+1);
				
				//
				textContentArr=weiboContent.split("@"+username);
				textContent=textContentArr[0]+textContentArr[1];
				temp=temp+atContent+tempnext;
				weiboContent=temp;
				
				//System.out.println(tempnext);
			}
			if(weiboContent.contains("#")){
				int topstartIndex=weiboContent.indexOf("#");//此处#话题,#号结束
				String temp=weiboContent.substring(0,topstartIndex);
				String tempnext=weiboContent.substring(topstartIndex);
				int topendIndex=tempnext.lastIndexOf("#");
				topic=tempnext.substring(1,topendIndex);
				topicContent="<a href=???><font color=red>#"+topic+"#</font></a>";//做变色处理，并且便于以后查询
				tempnext=tempnext.substring(topendIndex+1);
				//
				textContentArr=textContent.split("#"+topic+"#");
				textContent=textContentArr[0]+textContentArr[1];
				temp=temp+topicContent+tempnext;
				weiboContent=temp;
				
			}
			//敏感词检测,若没有，则插数据库，否则，不给插
			//if(textContent)
			//用户查询，若有，需要给用户提醒，并插入数据库
			//if(username)
			//话题查询，若有，插入话题的数据库，若没有，则不作操作
			//if(topic)
			FilterBanedKeywordsService detect=new FilterBanedKeywordsServiceImpl();
			if(detect.isBanedKeywordInside(weiboContent)){
				//给前端返回不可以
				System.out.println("前端显示不可以！！");
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
