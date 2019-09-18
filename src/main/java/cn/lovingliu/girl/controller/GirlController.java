package cn.lovingliu.girl.controller;


import cn.lovingliu.girl.common.ServerResponse;
import cn.lovingliu.girl.domain.Girl;
import cn.lovingliu.girl.exception.GirlException;
import cn.lovingliu.girl.repository.GirlRepository;
import cn.lovingliu.girl.service.GirlService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 廖师兄
 * 2016-11-03 23:15
 */
@RestController
public class GirlController {

    @Autowired
    private GirlRepository girlRepository;

    @Autowired
    private GirlService girlService;

    @ApiOperation(value = "获取女生列表",notes = "")
    @GetMapping(value = "/girls")
    public List<Girl> girlList() {
        return girlRepository.findAll();
    }

    @ApiOperation(value = "新增女生",notes = "根据女生对象生成新的女生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cupSize",value = "罩杯", required = false, dataType = "String"),
            @ApiImplicitParam(name = "age",value = "年龄", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "money", value = "资产",required = true, dataType = "Double")
    })
    @PostMapping(value = "/girls")
    public ServerResponse<Girl> girlAdd(@Valid Girl girl, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return ServerResponse.createByErrorMessage(bindingResult.getFieldError().getDefaultMessage());
        }
        return ServerResponse.createBySuccess(girlRepository.save(girl));
    }

    @ApiOperation(value = "查询女生",notes = "根据女生ID 查询详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    @GetMapping(value = "/girls/{id}")
    public Girl girlFindOne(@PathVariable("id") Integer id) {
        return girlRepository.findById(id).orElse(null);
    }

    //更新
    @PutMapping(value = "/girls/{id}")
    public Girl girlUpdate(@PathVariable("id") Integer id,
                           @RequestParam("cupSize") String cupSize,
                           @RequestParam("age") Integer age) {
        Girl girl = new Girl();
        girl.setId(id);
        girl.setCupSize(cupSize);
        girl.setAge(age);

        return girlRepository.save(girl);
    }

    //删除
    @DeleteMapping(value = "/girls/{id}")
    public void girlDelete(@PathVariable("id") Integer id) {

        girlRepository.deleteById(id);
    }

    //通过年龄查询女生列表
    @GetMapping(value = "/girls/age/{age}")
    public List<Girl> girlListByAge(@PathVariable("age") Integer age) {
        return girlRepository.findByAge(age);
    }

    @PostMapping(value = "/girls/two")
    public void girlTwo() {
        girlService.insertTwo();
    }

    @GetMapping("/girls/getAge/{id}")
    public void getAge(@PathVariable("id") Integer id) throws GirlException {
        girlService.getAgeRole(id);
    }
}
