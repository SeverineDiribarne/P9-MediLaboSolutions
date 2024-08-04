package com.medilabo_gui.medilabo_gui.services;

import com.medilabo_gui.medilabo_gui.security.MyMainUser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Service
public class UserPasswordService {

    public HashMap< String , MyMainUser> users = new HashMap<>();

}
