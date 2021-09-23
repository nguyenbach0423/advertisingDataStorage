<template>
    <form ref="uploadForm" @submit.prevent="submit" style="width : 380px">
        <div id="file-js-example" class="file has-name">
            <label class="file-label" id="uploadForm">
                <input class="file-input" type="file" name="resume" ref="file" v-on:change="selectFile" onfocus="this.value=''" required>
                <span class="file-cta">
                <span class="file-label">
                    Choose File
                </span>
                </span>
                <span class="file-name">
                    No file uploaded
                </span>
                <span></span>
                <input type="button" v-on:click="uploadFile" name="Upload" value="Upload" class="button" id="button"/>
            </label>
        </div>
    </form>
    <div class="notification-message">
        <span>{{message}}</span>
    </div>
    <span>You can download errors file here.</span>
    <div v-for="item, index in listFile" :key="index" style="margin-top:10px">
        <a @click="deleteItem(index, path+item, item)">{{ item }}</a>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: "UploadFile",

    data: () => ({
        formData : null,
        message : "",
        isnonActive : true,
        listFile : [],
        path : "http://localhost:8080/download/"
    }),

    created () {
        this.fetchData();
    },

    methods: {
        fetchData() {
            axios.get("http://localhost:8080/").then(response => {
                var list = response.data;
                for (let item of list)
                {
                    this.listFile.push(item);
                }
            })
        },

        deleteItem(index, pathLink, item) {
            axios({
                url: pathLink,
                method: 'GET',
                responseType: 'blob',
                }).then((response) => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', item);
                document.body.appendChild(link);
                link.click();
                });

            this.listFile.splice(index, 1);
        },

        selectFile() {
            this.file = this.$refs.file.files[0];
            this.formData = new FormData();
            this.formData.append("file", this.file);
            this.message = "";
            this.isnonActive = true;
            if (this.file.name.length > 0) {
                const fileName = document.querySelector('#file-js-example .file-name');
                fileName.textContent = this.file.name;
            }
        },

        uploadFile() {
            document.getElementById("button").disabled = true;
            setTimeout(function(){document.getElementById("button").disabled = false;},1000);
            
            const MAX_NAME_LENGTH = 250;
            const TYPE_FILE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            const MAX_SIZE = 10 * 1024 * 1024;
            
            if (this.file.name.length > MAX_NAME_LENGTH) {
                this.message = "File name too large."
            }
            else if (this.file.type != TYPE_FILE) {
                this.message = "File name must have extension '.xlsx'."
            }
            else if (this.file.size > MAX_SIZE) {
                this.message = "File too large."
            }
            else {
                axios({
                    url: "http://localhost:8080/upload",
                    method: "POST",
                    data: this.formData,
                    headers: {
                        Accept: 'application/json',
                        'Content-Type': 'multipart/form-data'
                    },
                }).then(response => {
                    console.log(response.data.message)
                    this.message = "File " + this.file.name + " upload successful.";
                }).catch((error) => {
                    console.log(error.response.data.message)
                    this.listFile.push(error.response.data.message);
                    this.message = "File " + this.file.name + " upload failed.";
                    this.isnonActive = false;
                });
            }
        },
    }
}
</script>

<style scoped>
    .notification-message {
        margin: 20px 0 5px 0;
        font-size: 15px;
    }
    
    .download-link {
        font-size: 15px;
    }

    .button {
        margin-left: 30px;
    }

    .nonactive {
        display: none;
    }
</style>