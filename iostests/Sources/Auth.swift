//
//  Keychain.swift
//  TestApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import auth

public struct Auth {
    
    // MARK: - Auth

    public static var handler: Auth {
        let identifier = "com.liftric.Keeper"
        return KVault(serviceName: identifier, accessGroup: nil)
    }
}
