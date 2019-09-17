package cn.lovingliu.girl.service;


import cn.lovingliu.girl.common.ResponseCode;
import cn.lovingliu.girl.domain.Girl;
import cn.lovingliu.girl.exception.GirlException;
import cn.lovingliu.girl.repository.GirlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by 廖师兄
 * 2016-11-04 00:08
 */
@Service
public class GirlService {

    @Autowired
    private GirlRepository girlRepository;

    @Transactional
    public void insertTwo() {
        Girl girlA = new Girl();
        girlA.setCupSize("A");
        girlA.setAge(18);
        girlRepository.save(girlA);


        Girl girlB = new Girl();
        girlB.setCupSize("B");
        girlB.setAge(19);
        girlRepository.save(girlB);
    }

    public void getAgeRole(Integer id) throws GirlException{
        Optional<Girl> optional = girlRepository.findById(id);
        Girl girl = optional.orElse(null);
        Integer age = girl.getAge();
        if(age < 10){
            //return ServerResponse.createBySuccessMessage("你可能上小学");
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能上小学");
        }else if(age > 10 && age < 16){
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能上初中");
        }else{
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能是个社会人");
        }
    }

    /**
     * @Desc 单元测试专用
     * @Author LovingLiu
    */
    public Girl findOne(Integer id){
        Optional<Girl> optional = girlRepository.findById(id);
        return optional.orElse(new Girl());
    }

}
