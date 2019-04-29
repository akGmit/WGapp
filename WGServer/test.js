let Server = require('./serverMain');
var ioc = require('socket.io-client');
let Client = require("./Client");

let s = new Server();

let user = new Client('1', '1', false, '1', '');
let client = ioc('ws://localhost:' + 31337);

beforeAll(() =>{
    s.start();
})


afterAll(() => {
    s.stop();
  });

describe('server functions', function () {
    
    it("should be succesfull login", (done) => {
        client.emit('log_in', JSON.stringify(user));
        client.on('log_in', (res) => {
            //expect(res).toEqual(true);
            expect(res).toBe(true);
            client.off();
            done();
        })
       
    });
    it("should be unsuccesfull login", (done) => {
        let failUser = new Client('3242', '1', false, '1', '');
        client.emit('log_in', JSON.stringify(failUser));
        client.on('log_in', (res) => {
            //expect(res).toEqual(true);
            expect(res).toBe(false);
            client.off();
            done();
        })
    })

    it("should be unsuccesful new user", (done) => {
        client.emit('new_user', JSON.stringify(user));
        client.on('new_user', (res) => {
            //expect(res).toEqual(true);
            expect(res).toBe(false);
            client.off();
            done();
        })
    })

    it("should be get workgroup list", (done) => {
        let validUser = new Client('1', '3244', false, '1', '');
        client.emit('new_group', JSON.stringify(validUser));
        client.on('user_join', () => {
            //expect(res).toEqual(true);
            expect.anything();
            client.off();
            done();
        })
    })

    it("should be workgroup list error", (done) => {
        let failUser = new Client('1', '3244', false, '1', '');
        client.emit('new_group', JSON.stringify(failUser));
        client.on('error_msg', () => {
            //expect(res).toEqual(true);
            expect.anything();
            client.off();
            done();
        })
    })

    it("should be succesfull join", (done) => {
        let validUSer = new Client('1', '3244', false, '1', '');
        client.emit('join_group', JSON.stringify(validUSer));
        client.on('user_join', () => {
            //expect(res).toEqual(true);
            expect.anything();
            client.off();
            done();
        })
    })


    it('should send and recieve message', (done) => {
        client.emit('message', true);
        client.on('message', (res) => {
            expect(res).toBe(true);
            //expect.anything();
            client.off();
            done();
        })
    })


    it("should be unsuccesfull join", (done) => {
        let failUser = new Client('1', '3244', false, '444', '');
        client.emit('join_group', JSON.stringify(failUser));
        client.on('error_msg', () => {
            //expect(res).toEqual(true);
            expect.anything();
            client.off();
            done();
        })
    })

    it("should leave group", (done) => {
       
        client.emit('leave_group');
        client.on('leave', (res)=>{
            expect(res).toBe(true);
            client.off();
            done();
        }
        )
            
            
    })

    it("should disconnect", (done) => {
        client.disconnect();
        expect(client.connected).toEqual(false);
        done();
       
    })
    
});
