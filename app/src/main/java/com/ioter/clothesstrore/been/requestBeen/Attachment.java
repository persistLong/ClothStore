package com.ioter.clothesstrore.been.requestBeen;

/**
 * 附件
 */
public class Attachment
{

    private int ID;
    private String FileName;
    private String FilePath;
    private String Memo;
    private String Status;
    private int CreateUser;
    private String CreateTime;
    private int UpdateUser;
    private String UpdateTime;
    private String FileFullPath;

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public int getID()
    {
        return ID;
    }

    public void setFileName(String FileName)
    {
        this.FileName = FileName;
    }

    public String getFileName()
    {
        return FileName;
    }

    public void setFilePath(String FilePath)
    {
        this.FilePath = FilePath;
    }

    public String getFilePath()
    {
        return FilePath;
    }

    public void setMemo(String Memo)
    {
        this.Memo = Memo;
    }

    public String getMemo()
    {
        return Memo;
    }

    public void setStatus(String Status)
    {
        this.Status = Status;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setCreateUser(int CreateUser)
    {
        this.CreateUser = CreateUser;
    }

    public int getCreateUser()
    {
        return CreateUser;
    }

    public void setCreateTime(String CreateTime)
    {
        this.CreateTime = CreateTime;
    }

    public String getCreateTime()
    {
        return CreateTime;
    }

    public void setUpdateUser(int UpdateUser)
    {
        this.UpdateUser = UpdateUser;
    }

    public int getUpdateUser()
    {
        return UpdateUser;
    }

    public void setUpdateTime(String UpdateTime)
    {
        this.UpdateTime = UpdateTime;
    }

    public String getUpdateTime()
    {
        return UpdateTime;
    }

    public void setFileFullPath(String FileFullPath)
    {
        this.FileFullPath = FileFullPath;
    }

    public String getFileFullPath()
    {
        return FileFullPath;
    }

}