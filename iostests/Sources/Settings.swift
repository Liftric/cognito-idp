//
//  Keychain.swift
//  TestApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import auth

public struct Settings {
    
    // MARK: - Settings

    public static var store: SettingsStore {
        SettingsStore()
    }
}
