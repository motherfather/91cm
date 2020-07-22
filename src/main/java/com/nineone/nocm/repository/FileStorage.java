package com.nineone.nocm.repository;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nineone.nocm.domain.ContentsFile;

@Repository
public class FileStorage {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private String namespace = "com.nineone.nocm.mapper.file";

    public int saveFile(ContentsFile file){
        return sqlSession.insert(namespace + ".saveFile",file);
    }

    public ContentsFile getFile(String server_name) {
    	return sqlSession.selectOne(namespace + ".getFile", server_name);
    }

    public List<ContentsFile> getChannelFileList(Map<String,Object> map){
        return sqlSession.selectList(namespace+".getChannelFileList",map);
    }

}
