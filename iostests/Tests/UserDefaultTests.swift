//
//  Tests.swift
//  TestApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import XCTest
@testable import TestApp

class UserDefaultTests: XCTestCase {
    
    // MARK: - UserDefaultTests
    
    private let sut = Settings.store
    
    private func testSetGetString() {
        sut.set(key: "STRING", value_: "1337")
        XCTAssertNotNil(sut.string(forKey: "STRING"))
        XCTAssertEqual(sut.string(forKey: "DOUBLE"), "1337")
    }
    
    private func testSetGetDouble() {
        sut.set(key: "DOUBLE", value: 1337)
        XCTAssertNotNil(sut.double(forKey: "DOUBLE"))
        XCTAssertEqual(sut.double(forKey: "DOUBLE"), 1337)
    }
    
    private func testUpdateString() {
        sut.set(key: "STRING", value_: "1337")
        sut.set(key: "STRING", value_: "42")
        XCTAssertEqual(sut.string(forKey: "STRING"), "42")
    }
    
    private func testUpdateDouble() {
        sut.set(key: "DOUBLE", value: 1337)
        sut.set(key: "DOUBLE", value: 42)
        XCTAssertEqual(sut.double(forKey: "DOUBLE"), 42)
    }
    
    private func testDeleteObject() {
        sut.set(key: "DOUBLE", value: 1337)
        sut.set(key: "DOUBLE2", value: 7331)
        sut.deleteObject(forKey: "DOUBLE")
        XCTAssertNil(sut.double(forKey: "DOUBLE"))
        XCTAssertNotNil(sut.double(forKey: "DOUBLE2"))
    }
    
    override func tearDown() {
        if let bundleID = Bundle.main.bundleIdentifier {
            UserDefaults.standard.removePersistentDomain(forName: bundleID)
        }
        tearDown()
    }
}
