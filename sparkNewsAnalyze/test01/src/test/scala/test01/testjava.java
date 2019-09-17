package test01;

public class testjava {
    private String oldBody="";

    public String lan(String event){
       oldBody=event;
       int length=oldBody.length();
       int flag=0;
        String info="";
        for(int i=0;i<length;i++)
        {
            if(oldBody.charAt(i) == ',')
            {
                flag++;
            }
            if(flag==2)
            {
                if (oldBody.charAt(i)!=',')
                info+=oldBody.charAt(i);

            }
            if(flag==3)
            {
                break;
            }

        }
//00:00:01,7993461044162651,[黑帮千金要结婚],7,2,www.tudou.com/programs/view/xD7r91DeI3o/
event=info;

        return event;
    }
    public static void main(String[] args){
        testjava tj=new testjava();
        String p=tj.lan("00:00:01,7993461044162651,[黑帮千金要结婚],7,2,www.tudou.com/programs/view/xD7r91DeI3o/");
        String q=tj.lan("00:00:01,8761939261737872,[年轻人住房问题],11,7,news.qq.com/a/20070810/002446.htm");
        System.out.println(q);
        System.out.println(p);
    }

}
