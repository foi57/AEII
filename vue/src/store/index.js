import { defineStore } from 'pinia'
export const Store = defineStore('user', {
    state: () => ({
            usersId: null,
            usersAvatar: null,
            usersName: null,
            usersPhone: null,
            usersEmail: null,
            usersRole: null,
            usersStatus: null,
            usersCreationTime: null,
    }),
    persist: {
        enabled: true,
        strategies: [
            {
                key: 'userStore',
                storage: localStorage,
                paths: ['usersId', 'usersName', 'usersRole','usersAvatar'] // 指定需要持久化的字段
            }
        ]
    },
        actions: {
            setUserInfo(userData) {  // 修改参数为对象形式
                this.usersId = userData.usersId;
                this.usersAvatar = userData.usersAvatar;
                this.usersName = userData.usersName;
                this.usersPhone = userData.usersPhone;
                this.usersEmail = userData.usersEmail;
                this.usersRole = userData.usersRole;
                this.usersStatus = userData.usersStatus;
                this.usersCreateTime = userData.usersCreationTime; // 注意字段名对应
            },
            setAvatar(avatar) {
                this.usersAvatar = avatar;
            }
        },

    getters: {
        getUserId: (state) => state.userId,
        getUsersAvatar: (state) => state.usersAvatar,
        getUserName: (state) => state.userName,
        getUsersPhone: (state) => state.usersPhone,
        getUsersEmail: (state) => state.usersEmail,
        getUsersRole: (state) => state.usersRole,
        getUsersStatus: (state) => state.usersStatus,
        getUsersCreateTime: (state) => state.usersCreateTime,
    }
})